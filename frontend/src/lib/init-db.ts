import { db } from '../lib/db';

export async function initDatabase() {
  const conn = await db.getConnection();
  try {
    await conn.query(`CREATE TABLE IF NOT EXISTS brands (id INT AUTO_INCREMENT PRIMARY KEY,name VARCHAR(255) NOT NULL )
    `);
    console.log(" 'brands' table ensured.");
  } catch (err) {
    console.error(" Failed to create 'brands' table:", err);
  } finally {
    conn.release();
  }
}
