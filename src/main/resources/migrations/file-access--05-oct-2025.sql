alter table file
add column access ENUM('OPEN', 'FREE','PREMIUM') DEFAULT 'PREMIUM';
