UPDATE friendship
SET status = TRUE
WHERE (user_id = ? AND friend_id = ?)
   OR (user_id = ? AND friend_id = ?)