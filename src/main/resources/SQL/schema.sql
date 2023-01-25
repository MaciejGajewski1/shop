DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `product`;
CREATE TABLE `products` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(30) NOT NULL,
    `price` DECIMAL(6,2) NOT NULL,
    `cart_id` BIGINT,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_cart`
    FOREIGN KEY (`cart_id`) REFERENCES `cart`(`id`)
)

