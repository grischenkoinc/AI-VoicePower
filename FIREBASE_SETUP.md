# Firebase Setup Instructions

## 1. Create Firebase Project
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project" → name it "AI VoicePower"
3. Disable Google Analytics (optional) → Create project

## 2. Add Android App
1. In Firebase Console → Project settings → Add app → Android
2. Package name: `com.aivoicepower`
3. App nickname: "AI VoicePower"
4. Get SHA-1 fingerprint:
   ```bash
   cd "D:\AIDevelop\AI VoicePower"
   ./gradlew signingReport
   ```
   Copy the SHA-1 from the debug variant
5. Register app → Download `google-services.json`
6. Place `google-services.json` in `app/` directory

## 3. Enable Authentication
1. Firebase Console → Authentication → Get started
2. Sign-in method tab → Enable:
   - **Email/Password** → Enable → Save
   - **Google** → Enable → Set project support email → Save
3. Copy the **Web client ID** from Google provider settings

## 4. Set Web Client ID
Add to `app/src/main/res/values/strings.xml`:
```xml
<string name="default_web_client_id">YOUR_WEB_CLIENT_ID_HERE</string>
```

## 5. Create Firestore Database
1. Firebase Console → Firestore Database → Create database
2. Start in **production mode**
3. Choose location closest to your users (europe-west1 for Ukraine)
4. Set security rules:
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

## 6. Create Storage Bucket
1. Firebase Console → Storage → Get started
2. Start in **production mode**
3. Set security rules:
```
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /recordings/{userId}/{allPaths=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
      allow read, write: if request.resource.size < 50 * 1024 * 1024; // 50MB max
    }
  }
}
```

## 7. Verify Setup
After completing all steps, build the project:
```bash
./gradlew assembleDebug
```
The build should succeed if `google-services.json` is correctly placed.
