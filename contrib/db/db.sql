-- Create the db tables that will be used by hibernate.

drop table if exists testvectors;
-- The DB that acts as a cache for testvectors. If a lookup for a plaintext/cipher/key combination is succesfull, no new vector needs to be generated.
create table testvectors (
        id int(10) unsigned not null auto_increment,
        cipher varchar(10) not null,
        key varchar(512) not null,
        plaintext varchar(512) not null, 
        ciphertext varchar(512) not null
)

-- TODO: Do the same thing for asymetric ciphers.
