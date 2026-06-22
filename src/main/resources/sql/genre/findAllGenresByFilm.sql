SELECT *
FROM GENRES g,
     film_genres fg
WHERE fg.genre_id = g.genre_id
  AND fg.film_id IN (%s)