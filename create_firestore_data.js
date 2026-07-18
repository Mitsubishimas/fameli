const admin = require('firebase-admin');

// Если нет ключа сервисного аккаунта — создаём тестовый скрипт
console.log('Для создания структуры Firestore:');
console.log('1. Скачай ключ сервисного аккаунта из Firebase Console');
console.log('2. Settings → Service accounts → Generate new private key');
console.log('3. Сохрани как service-account.json');
console.log('4. Запусти: node create_firestore_data.js');
