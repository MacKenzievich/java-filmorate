SELECT f.film_id,
       f.name,
       f.description,
       f.releaseDate,
       f.duration,
       mpa.rating_id,
       mpa.name AS mpa_name
FROM films AS f
         INNER JOIN mpa_rating AS mpa ON f.rating_id = mpa.rating_id