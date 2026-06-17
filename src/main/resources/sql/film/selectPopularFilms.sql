LEFT JOIN likes ON f.film_id = likes.film_id
GROUP BY f.film_id
ORDER BY COUNT(likes.film_id) DESC
LIMIT ?