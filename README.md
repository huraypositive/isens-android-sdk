# Isens Android SDK
아이센스 혈당계 SDK의 사용을 단순하게 하기 위한 라이브러리

## SDK 파일
- [isenssdk](/isensdk/libs) 폴더 안에 jar포멧의 라이브러리 파일로 추가되어있다.
- 향후 기기의 펌웨어 업데이트 등의 이슈로 sdk 업데이트가 필요한 경우 아이센스 제조사에서 sdk를 새로 받아서 교체해줘야 한다.

## Jitpack 배포
해당 라이브러리는 jitpack으로 배포되었으며, 아래의 과정을 통해 새 버전을 배포할 수 있다.
- 깃허브 저장소에서 새로운 태그를 지정한다.
- [Jitpack](https://jitpack.io/) 홈페이지의 `Git repo url` 검색창에 `huraypositive/isens-android-sdk`를 넣어 repo를 찾은 후, 새로 추가한 태그의 버전을 빌드한다.
- 안드로이드 프로젝트에서 새로 빌드된 버전으로 업데이트하여 정상 배포되었는지를 테스트한다.

## 적용 기기 목록
- CareSens N Premier

## 의존성 추가
```gradle
// in your settings.gradle
dependencyResolutionManagement {
    repositories {
        ...
        mavenCentral()
        maven { url "https://jitpack.io" }
    }
}

// in your app-level build.gradle
dependencies {
    implementation 'com.github.huraypositive:isens-android-sdk:$version'
}
```

## Sample Code
- [sample](/sample) 모듈 참고

## 사용 방법

## Manifest.xml 권한 추가
```xml
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
```

## 기기 스캔 및 등록 (Register)
#### IsensManager 객체 초기화
```kotlin
private val isensManager = IsensManager.getIsensManager(applicationContext, isensCallback)
```

#### IsensManager를 사용할 클래스에서 IsensCallback 인터페이스를 구현한다.
```kotlin
class DemoViewModel : ViewModel(), IsensCallback {
    private val isensManager = IsensManager.getIsensManager(applicationContext, isensCallback)
    
    override fun onScanned(devices: List<DiscoveredIsensDevice>) {
        // 스캔된 기기 목록이 들어오는 콜백
    }

    override fun onConnectionSuccess() {
        // 기기 등록이 성공했을 때 호출되는 콜백
        // 단순히 기기 등록만 필요한 경우 여기에서 혈당계의 주소(UUID)를 저장하면 되고, 
        // 등록과 동시에 혈당 기록을 불러오려면 여기서 isensManager.requestAllRecords() 함수를 호출한다.
    }

    override fun onReceiveData(records: List<IsensGlucoseRecord>) {
        // 요청된 혈당 기록값이 들어오는 콜백 
    }

    override fun onError(error: IsensError) {
        // 에러 발생 콜백 
    }

    override fun onDataEmpty() {
        // 기록 전송 요청이 성공했으나 가져올 기록이 없는 경우 호출되는 콜백
    }
}
```

## IsensManager 인터페이스
### 스캔 시작
```kotlin
fun startScan(deviceType: IsensDeviceType)
```

### 기기 연결
```kotlin
fun connectDevice(address: String)
```

### 혈당기록 요청 (sequenceNumber 이후 기록만 가져옴)
예를 들어 아래 함수로 넘겨주는 `sequenceNumber`가 10이라면 데이터 요청 시 기록값의 `sequenceNubmer`가 10보다 크거나 작은 기록들만 전송한다. 기록이 없을 경우 onDataEmpty() 콜백이 호출된다. 
```kotlin
fun requestRecordsAfter(sequenceNumber: Int)
```

### 모든 혈당기록 요청
`sequenceNumber`와 상관없이 혈당계에 저장된 모든 기록을 전송받을 때 사용한다.
```kotlin
fun requestAllRecords()
```

### 안드로이드 기기와 시간 동기화
```kotlin
fun requestTimeSync()
```

### 스캔 중지
```kotlin
fun stopScan()
```

### 연결 해제
더이상 SDK를 사용하지 않거나 혹은 즉시 연결을 끊어야 할 때 호출한다.
```kotlin
fun disconnect()
```
