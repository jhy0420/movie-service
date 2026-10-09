INSERT INTO movie (id, title, vote_average, vote_count, release_date, revenue, runtime, backdrop_path, budget, homepage, overview, popularity, poster_path, director, genres, themes)
VALUES
(1, 'Inception', 8.4, 3000, '2010-07-16', 836800000, 148, '/inception-backdrop.jpg', 160000000, 'https://inception.example.com', 'A thief who steals secrets through dreams.', 90.0, '/inception-poster.jpg', 'Christopher Nolan', ARRAY['Action', 'Sci-Fi'], ARRAY['dreams', 'heist']),
(2, 'Interstellar', 8.6, 2500, '2014-11-05', 701700000, 169, '/interstellar-backdrop.jpg', 165000000, 'https://interstellar.example.com', 'Explorers travel through a wormhole.', 80.0, '/interstellar-poster.jpg', 'Christopher Nolan', ARRAY['Sci-Fi', 'Drama'], ARRAY['space', 'time']),
(3, 'The Godfather', 8.7, 5000, '1972-03-14', 245000000, 175, '/godfather-backdrop.jpg', 6000000, NULL, 'The aging patriarch of a crime dynasty.', 70.0, '/godfather-poster.jpg', 'Francis Ford Coppola', ARRAY['Crime', 'Drama'], ARRAY['family']),
(4, 'Dunkirk', 7.5, 1000, '2017-07-21', 527000000, 106, '/dunkirk-backdrop.jpg', 100000000, NULL, 'Allied soldiers are evacuated.', 60.0, '/dunkirk-poster.jpg', 'Christopher Nolan', ARRAY['action', 'War'], ARRAY['survival']),
(5, 'Unreleased Draft', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, ARRAY['Drama'], NULL);
