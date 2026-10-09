INSERT INTO movie (id, title, vote_count, release_date, popularity, genres)
SELECT 100 + n, 'Bulk Movie ' || n, n, DATE '2000-01-01' + n, n, ARRAY['Bulk']
FROM generate_series(1, 25) AS n;
