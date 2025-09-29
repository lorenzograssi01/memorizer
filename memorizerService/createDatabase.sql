CREATE DATABASE IF NOT EXISTS `620944`;
USE `620944`;

DROP TABLE IF EXISTS Words;
CREATE TABLE IF NOT EXISTS Words(
	word VARCHAR(31) PRIMARY KEY,
    `description` VARCHAR(255),
    translation VARCHAR(31),
    quizzed INTEGER DEFAULT 0,
    guessed INTEGER DEFAULT 0
);

INSERT INTO Words(word, description, translation, quizzed, guessed) VALUES 
('Apple', 'A round fruit with red, green, or yellow skin and a firm white flesh', 'Mela', 50, 29),
('Book', 'A set of written or printed pages bound together', 'Libro', 44, 1),
('Cat', 'A small domesticated carnivorous mammal with soft fur', 'Gatto', 38, 10),
('Dog', 'A domesticated carnivorous mammal typically kept as a pet', 'Cane', 11, 9),
('Elephant', 'A large mammal with a trunk native to Africa and Asia', 'Elefante', 67, 39),
('Flower', 'The seed-bearing part of a plant, often colorful and fragrant', 'Fiore', 27, 4),
('Guitar', 'A stringed musical instrument played with fingers or a pick', 'Chitarra', 51, 38),
('House', 'A building for human habitation', 'Casa', 43, 7),
('Island', 'A piece of land surrounded by water', 'Isola', 70, 10),
('Jacket', 'A short coat typically extending to the hips', 'Giacca', 88, 41),
('Kite', 'A toy consisting of a light frame covered with paper or cloth, flown in the wind', 'Aquilone', 68, 52),
('Lamp', 'A device for giving light', 'Lampada', 82, 10),
('Mountain', 'A large natural elevation of the earth''s surface', 'Montagna', 63, 49),
('Notebook', 'A book of blank pages for writing notes', 'Quaderno', 36, 2),
('Ocean', 'A vast body of salt water that covers most of the earth''s surface', 'Oceano', 94, 42),
('Pencil', 'An instrument for writing or drawing, typically made of wood and graphite', 'Matita', 64, 47),
('Question', 'A sentence or phrase used to find out information', 'Domanda', 64, 43),
('River', 'A large natural stream of water flowing into a sea, lake, or another river', 'Fiume', 2, 0),
('Sun', 'The star at the center of our solar system', 'Sole', 13, 3),
('Tree', 'A woody perennial plant with a trunk and branches', 'Albero', 89, 2),
('Umbrella', 'A device for protection from rain or sun, consisting of a fabric stretched over a folding frame', 'Ombrello', 56, 32),
('Violin', 'A stringed instrument played with a bow', 'Violino', 78, 55),
('Window', 'An opening in a wall or door that usually contains glass', 'Finestra', 45, 20),
('Xylophone', 'A musical instrument with wooden bars struck by mallets', 'Xilofono', 34, 18),
('Yacht', 'A medium-sized sailing boat, often used for recreation', 'Yacht', 25, 14),
('Zebra', 'An African wild horse with black-and-white stripes', 'Zebra', 67, 30),
('Ball', 'A round object typically used in games or sports', 'Palla', 49, 24),
('Clock', 'A device for measuring and displaying time', 'Orologio', 85, 72),
('Desk', 'A piece of furniture with a flat surface for writing or working', 'Scrivania', 58, 40),
('Egg', 'An oval object laid by female birds and reptiles, often eaten as food', 'Uovo', 41, 33),
('Fan', 'A device with rotating blades that creates a flow of air', 'Ventilatore', 62, 51),
('Garden', 'An area of ground used for growing flowers or vegetables', 'Giardino', 78, 65);