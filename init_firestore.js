const admin = require('firebase-admin');
const serviceAccount = require('./service-account.json');

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    projectId: 'fameli-private'
});

const db = admin.firestore();

async function initializeFirestore() {
    console.log('🔥 Инициализация Firestore...\n');

    // 1. Создаём тестовую семью
    const familyRef = db.collection('families').doc('test_family');
    await familyRef.set({
        name: 'Моя семья',
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
        createdBy: 'system'
    });
    console.log('✅ Семья создана');

    // 2. Добавляем админа
    const adminUserRef = familyRef.collection('members').doc('admin_uid');
    await adminUserRef.set({
        role: 'ADMIN',
        name: 'Папа',
        email: 'dad@example.com',
        joinedAt: admin.firestore.FieldValue.serverTimestamp()
    });
    console.log('✅ Админ добавлен');

    // 3. Создаём тестовые категории
    const categories = [
        {
            cloudId: 'cat_food_001',
            name: 'Продукты',
            type: 'EXPENSE',
            icon: '🛒',
            color: 0xFF1B6B4A,
            isDeleted: false,
            lastModified: Date.now()
        },
        {
            cloudId: 'cat_transport_001',
            name: 'Транспорт',
            type: 'EXPENSE',
            icon: '🚗',
            color: 0xFF4A6572,
            isDeleted: false,
            lastModified: Date.now()
        },
        {
            cloudId: 'cat_entertainment_001',
            name: 'Развлечения',
            type: 'EXPENSE',
            icon: '🎮',
            color: 0xFF7C5800,
            isDeleted: false,
            lastModified: Date.now()
        },
        {
            cloudId: 'cat_salary_001',
            name: 'Зарплата',
            type: 'INCOME',
            icon: '💼',
            color: 0xFF03DAC5,
            isDeleted: false,
            lastModified: Date.now()
        }
    ];

    for (const category of categories) {
        await familyRef.collection('categories').doc(category.cloudId).set(category);
    }
    console.log('✅ Категории созданы');

    // 4. Создаём тестовый бюджет
    const budgetRef = familyRef.collection('budgets').doc('budget_2026_07');
    await budgetRef.set({
        cloudId: 'budget_2026_07',
        categoryCloudId: null, // общий бюджет
        limitAmount: 80000,
        month: '2026-07',
        alertThreshold: 0.8,
        isDeleted: false,
        lastModified: Date.now()
    });
    console.log('✅ Бюджет создан');

    // 5. Создаём тестовые транзакции
    const transactions = [
        {
            cloudId: 'txn_001',
            categoryCloudId: 'cat_food_001',
            amount: 2500,
            currency: 'RUB',
            date: Date.now() - 86400000, // вчера
            note: 'Пятёрочка',
            isDeleted: false,
            lastModified: Date.now() - 86400000
        },
        {
            cloudId: 'txn_002',
            categoryCloudId: 'cat_transport_001',
            amount: 500,
            currency: 'RUB',
            date: Date.now() - 43200000, // 12 часов назад
            note: 'Такси',
            isDeleted: false,
            lastModified: Date.now() - 43200000
        },
        {
            cloudId: 'txn_003',
            categoryCloudId: 'cat_salary_001',
            amount: 120000,
            currency: 'RUB',
            date: Date.now() - 259200000, // 3 дня назад
            note: 'Зарплата за июль',
            isDeleted: false,
            lastModified: Date.now() - 259200000
        }
    ];

    for (const transaction of transactions) {
        await familyRef.collection('transactions').doc(transaction.cloudId).set(transaction);
    }
    console.log('✅ Тестовые транзакции созданы');

    console.log('\n🎉 Firestore инициализирован!');
    console.log('📂 Структура:');
    console.log('   families/test_family/');
    console.log('   ├── members/admin_uid');
    console.log('   ├── categories/ (4 категории)');
    console.log('   ├── budgets/ (1 бюджет)');
    console.log('   └── transactions/ (3 транзакции)');
}

initializeFirestore()
    .then(() => process.exit(0))
    .catch((error) => {
        console.error('❌ Ошибка:', error);
        process.exit(1);
    });
