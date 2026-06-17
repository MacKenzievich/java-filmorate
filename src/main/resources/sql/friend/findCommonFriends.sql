SELECT u.user_id, u.email, u.login, u.name, u.birthday
FROM friendship AS f
         INNER JOIN friendship fr ON fr.friend_id = f.friend_id
         INNER JOIN users u ON u.user_id = fr.friend_id
WHERE f.user_id = ?
  AND fr.user_id = ?
  AND f.friend_id <> fr.user_id
  AND fr.friend_id <> f.user_id