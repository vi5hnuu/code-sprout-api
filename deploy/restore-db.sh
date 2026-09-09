#!/usr/bin/env bash
# code-sprout-api — restore MySQL from a .sql / .sql.gz dump.
# Usage: sudo ./deploy/restore-db.sh <path/to/dump.sql[.gz]>
set -euo pipefail

ENV=/etc/code-sprout/prod.env
DB=code_sprout
SVC=code-sprout-mysql

die() { echo "[restore-db] $*" >&2; exit 1; }
[[ $EUID -eq 0 ]] || die "run as root"
[[ $# -ge 1 && -f "$1" ]] || die "usage: $0 <dump.sql[.gz]>"

dump=$1
root=$(awk -F= '$1=="MYSQL_ROOT_PASSWORD"{print substr($0,index($0,"=")+1); exit}' "$ENV")
[[ -n "$root" ]] || die "MYSQL_ROOT_PASSWORD missing in $ENV"

container=$(docker ps --format '{{.Names}}' | grep -m1 "$SVC") || die "$SVC container not running"
read -r -p "Overwrite $DB in $container from $dump ? [y/N] " r
[[ "$r" == y* || "$r" == Y* ]] || die "aborted"

docker exec -i "$container" mysql -uroot -p"$root" \
    -e "DROP DATABASE IF EXISTS \`$DB\`; CREATE DATABASE \`$DB\` DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;"

if [[ "$dump" == *.gz ]]; then
    gunzip -c "$dump" | docker exec -i "$container" mysql -uroot -p"$root" "$DB"
else
    docker exec -i "$container" mysql -uroot -p"$root" "$DB" < "$dump"
fi

echo "[restore-db] restored. tables:"
docker exec -i "$container" mysql -uroot -p"$root" "$DB" -e "SHOW TABLES;"
