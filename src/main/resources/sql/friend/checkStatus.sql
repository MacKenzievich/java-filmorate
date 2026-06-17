SELECT COUNT(*)
FROM friendship
WHERE user_id = ?
  AND friend_id = ?