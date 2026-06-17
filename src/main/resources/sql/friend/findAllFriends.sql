SELECT u.user_id, u.email, u.login, u.name, u.birthday
FROM friendship AS f
         INNER JOIN users AS u ON u.user_id = f.friend_id
WHERE f.user_id = ?
ORDER BY u.user_id