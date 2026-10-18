ملف دمج التعديلات - دمج-PATCH.md

ملخص سريع للدمج:

1) المسارات المقترحة:
   - ضع SafeSaveHelper.kt تحت package: com.ma2i.water.util.safesave
   - ضع PasswordUtils.kt تحت: com.ma2i.water.security
   - ضع BackupCrypto.kt تحت: com.ma2i.water.backup
   - ضع RecoveryActivity.kt تحت: com.ma2i.water.ui.recovery
   - ضع layout res/layout/activity_recovery.xml

2) تحديث build.gradle (module: app)
   - تأكد من وجود الاعتمادات:
     implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"
     implementation "androidx.security:security-crypto:1.1.0" // مستحسن

3) رابط دمج SafeSaveHelper:
   - في أي Activity/Fragment/VM حيث تنفذ عملية الحفظ:
     a) عرض ProgressIndicator فور الضغط
     b) استدعاء SafeSaveHelper.safeSave(...) وتمرير suspend dbSave
     c) onSuccess: إخفاء المؤشر + إظهار رسالة نجاح
     d) onError: إظهار AlertDialog مع خيارات إعادة المحاولة/حفظ كمسودة/تصدير

4) تخزين كلمة السر (PasswordUtils):
   - عند الإنشاء/التغيير: salt = PasswordUtils.generateSalt(); hash = PasswordUtils.hashPassword(password, salt)
   - خزّن hash و salt مشفّرين (EncryptedSharedPreferences أو DB مشفّر)

5) تنسيق Backup file:
   - header JSON: { "version":1, "salt":"<base64>", "payload":"<base64_iv+ciphertext>" }
   - عند التصدير: اطلب كلمة سر -> deriveKey -> encryptToBase64 -> احفظ header+payload
   - عند الاستيراد: اقرأ header -> اطلب كلمة سر -> deriveKey -> decryptFromBase64 -> إذا فشل: لا تمسح DB واظهر RecoveryActivity

6) اختبارات مقترحة:
   - PasswordUtils: اختبار hash و verify
   - BackupCrypto: اختبار encrypt -> decrypt roundtrip
   - SafeSaveHelper: اختبار نجاح وفشل (محاكاة IOException)

7) ملاحظات:
   - قبل النشر: اطلب من المستخدمين عمل Export يدوياً كاحتياط.
   - اختبر سيناريوهات الترحيل بعناية.
