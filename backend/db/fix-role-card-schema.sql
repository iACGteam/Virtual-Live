-- Fix role_cards schema to support large Base64 images
USE virtuallive_dev;

-- Change portrait column to LONGTEXT to support large Base64 strings (up to 4GB)
ALTER TABLE role_cards MODIFY COLUMN portrait LONGTEXT;

-- Also ensure background_story is TEXT (64KB should be enough, but let's be safe)
ALTER TABLE role_cards MODIFY COLUMN background_story TEXT;

SELECT 'Role cards schema updated successfully' AS status;
