#!/usr/bin/env bash
# code-sprout-api — one-shot homelab setup + build + start. Idempotent.
set -euo pipefail
here=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
root=$(cd "$here/.." && pwd)

SITE=code-sprout-api.laxmi.solutions
ENV_SRC=$root/.env
ENV_DST=/etc/code-sprout/prod.env
DATA=/var/lib/code-sprout/mysql
CADDYFILE=/etc/caddy/Caddyfile
CADDY_IMPORT=/etc/caddy/code-sprout.caddy
UNIT=/etc/systemd/system/code-sprout.service
DB=code_sprout

log() { printf '\033[1;36m[code-sprout]\033[0m %s\n' "$*"; }
die() { printf '\033[1;31m[code-sprout]\033[0m %s\n' "$*" >&2; exit 1; }

[[ $EUID -eq 0 ]] || die "run as root (sudo -E $0)"
[[ -f "$ENV_SRC" ]] || die "missing $ENV_SRC — copy the prod env in place first"
command -v docker >/dev/null || die "docker missing"
command -v caddy  >/dev/null || die "caddy missing"

# ── env: strip host-varying keys from source, rewrite DB_HOST to the JDBC
#    URL that points at the compose sibling, add MYSQL container-init vars
#    (user + database + root password) derived from DB_*. Preserve a
#    previously-generated MYSQL_ROOT_PASSWORD across re-runs so the DB
#    doesn't lock itself out.
install -d -m 0750 /etc/code-sprout
prev_root=$(awk -F= '$1=="MYSQL_ROOT_PASSWORD"{print substr($0,index($0,"=")+1); exit}' "$ENV_DST" 2>/dev/null || true)

tmp=$(mktemp)
grep -v '^DB_HOST=\|^MYSQL_ROOT_PASSWORD=\|^MYSQL_USER=\|^MYSQL_PASSWORD=\|^MYSQL_DATABASE=\|^SPRING_JPA_HIBERNATE_DDL_AUTO=\|^AWS_ACCESS_KEY=\|^AWS_SECRET_KEY=' "$ENV_SRC" > "$tmp"

echo "DB_HOST=jdbc:mysql://code-sprout-mysql:3306/$DB" >> "$tmp"

# App reads the bare AWS_ACCESS_KEY / AWS_SECRET_KEY (see
# application-prod.properties). Source .env carries them as
# _PRIMARY/_SECONDARY; alias PRIMARY → bare so Spring resolves them.
for pair in "AWS_ACCESS_KEY_PRIMARY:AWS_ACCESS_KEY" "AWS_SECRET_KEY_PRIMARY:AWS_SECRET_KEY"; do
    src=${pair%%:*}; dst=${pair##*:}
    v=$(awk -F= -v k="$src" '$1==k{print substr($0,index($0,"=")+1); exit}' "$tmp")
    [[ -n "$v" ]] && echo "$dst=$v" >> "$tmp"
done

db_user=$(awk -F= '$1=="DB_USERNAME"{print substr($0,index($0,"=")+1); exit}' "$tmp")
db_pass=$(awk -F= '$1=="DB_PASSWORD"{print substr($0,index($0,"=")+1); exit}' "$tmp")
[[ -n "$db_user" ]] || die "DB_USERNAME not set in $ENV_SRC"
[[ -n "$db_pass" ]] || die "DB_PASSWORD not set in $ENV_SRC"

echo "MYSQL_USER=$db_user"                                  >> "$tmp"
echo "MYSQL_PASSWORD=$db_pass"                              >> "$tmp"
echo "MYSQL_DATABASE=$DB"                                   >> "$tmp"
echo "MYSQL_ROOT_PASSWORD=${prev_root:-$db_pass}"           >> "$tmp"
# First boot needs Hibernate to generate the schema; deploy/restore-db.sh
# flips this back to `validate` after the RDS dump lands (or you can do it
# manually). Comment: safe to leave as `update` if you prefer.
echo "SPRING_JPA_HIBERNATE_DDL_AUTO=update"                 >> "$tmp"

install -m 0600 -o root -g root "$tmp" "$ENV_DST"
rm -f "$tmp"
log "wrote $ENV_DST"

# ── data dir (mysql UID 999 in the official image).
install -d -m 0700 -o 999 -g 999 "$DATA"

# ── systemd unit.
install -m 0644 "$here/code-sprout.service" "$UNIT"
systemctl daemon-reload
systemctl enable code-sprout.service >/dev/null 2>&1 || true

# ── caddy snippet + site block (only append if missing).
install -m 0644 "$here/code-sprout.caddy" "$CADDY_IMPORT"
if ! grep -qE "^${SITE//./\\.}[[:space:]]*\{" "$CADDYFILE"; then
    cp -a "$CADDYFILE" "$CADDYFILE.bak.$(date -u +%Y%m%d-%H%M%SZ)"
    printf '\n%s {\n\timport %s\n}\n' "$SITE" "$CADDY_IMPORT" >> "$CADDYFILE"
    log "appended site block for $SITE"
fi
caddy validate --config "$CADDYFILE" --adapter caddyfile >/dev/null
systemctl reload caddy
log "caddy reloaded"

# ── build image if missing (first-time build downloads Maven deps).
if ! docker image inspect code-sprout-service:1.0 >/dev/null 2>&1; then
    log "building code-sprout-service:1.0 (first-time build)"
    (cd "$root" && docker compose build)
fi

log "starting code-sprout.service"
systemctl restart code-sprout.service
systemctl --no-pager status code-sprout.service | head -8 || true
log "done — https://$SITE (once DNS points here)"
