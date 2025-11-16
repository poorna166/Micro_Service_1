import mysql from 'mysql2/promise';

export const db = mysql.createPool({

    
    host: process.env.NEXT_PUBLIC_DB_HOST, // e.g., 'localhost'
    user: process.env.NEXT_PUBLIC_DB_USER, // your MariaDB username
    password: process.env.NEXT_PUBLIC_DB_PASS, // your MariaDB password
    database: process.env.NEXT_PUBLIC_DB_NAME, // your database name
    //port: process.env.NEXT_PUBLIC_DB_PORT ? parseInt(process.env.NEXT_PUBLIC_DB_PORT, 10) : undefined // usually 3306
    // host: 'localhost' ,
    // user: 'root',
    // password: 'dsquad123',
    // database: 'skinflex', 
    

   


});
