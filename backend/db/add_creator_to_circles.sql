
USE virtuallive_dev;

-- Add creator_id to fan_circles table
ALTER TABLE fan_circles
ADD COLUMN creator_id INT NULL;

-- Add foreign key constraint
ALTER TABLE fan_circles
ADD CONSTRAINT fk_circle_creator
FOREIGN KEY (creator_id) REFERENCES users(user_id)
ON DELETE SET NULL;

-- Create index for creator_id
CREATE INDEX idx_creator ON fan_circles(creator_id);
