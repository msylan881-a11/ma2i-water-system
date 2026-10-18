# تقرير تفكيك APK واستخراج الموارد

## النتيجة

تم فك نسخة العمل من الملف المرفق باستخدام Apktool 2.7.0. بقي ملف APK الأصلي في مكانه دون تعديل، وتطابق بصمته بعد الاستخراج مع بصمته قبله. كما اجتاز الأرشيف اختبار سلامة ZIP، واجتاز توقيعه تحقق `apksigner`.

بيانات الحزمة المستخرجة: `com.maai.app`، الإصدار `1.1.1`، `versionCode=12`، الحد الأدنى Android API 23، والهدف/الترجمة API 36.

## محتوى الاستخراج

- `apktool-decoded/assets/index.html`: واجهة التطبيق ومنطق JavaScript، نحو 138 KB.
- `apktool-decoded/assets/privacy-policy.html` وملفات أيقونات و`manifest.json`.
- `apktool-decoded/AndroidManifest.xml`: بيان Android مفكوك، ويعرّف `MainActivity` و`ShareFileProvider`.
- `apktool-decoded/res/`: 24 ملف مورد مفكوك، بما فيها الأيقونات والثيمات وملفات XML.
- `apktool-decoded/smali/`: 13 ملف Smali مستخرج من DEX، بما فيها `MainActivity` و`ShareFileProvider` وطبقة الجسر الأصلية.
- `apktool-decoded/apktool.yml` و`apktool-decoded/original/`: بيانات بناء ونسخ أصلية من عناصر APK ذات الصلة.

المجموع 51 ملفًا داخل مجلد فك الترميز.

## حدود هذه المرحلة

Smali هو تمثيل تجميعي للـDEX وليس ملفات Java/Kotlin الأصلية، ولا يحتوي الاستخراج على `build.gradle` أو Gradle Wrapper أو ملفات مشروع Android Studio. لذلك الناتج مصدر مرجعي لإعادة الإنشاء، وليس مشروعًا جاهزًا لبناء AAB. سيحتاج العمل التالي إلى تحويل الغلاف الأصلي إلى مصدر Java/Kotlin قابل للصيانة، وإعداد مشروع Gradle جديد، ثم اختبار جسر JavaScript والتخزين والتصدير والاستيراد.

لا يحتوي APK على سجلات العملاء أو المعاملات المخزنة محليًا على أجهزة المستخدمين؛ هذه البيانات ليست جزءًا من حزمة التطبيق ولا يستخرجها هذا الإجراء.

## المسارات

- مجلد فك الترميز: `apktool-decoded/`
- واجهة التطبيق: `apktool-decoded/assets/index.html`
- البيان: `apktool-decoded/AndroidManifest.xml`
- سجل Apktool: `apktool-decode.log`
