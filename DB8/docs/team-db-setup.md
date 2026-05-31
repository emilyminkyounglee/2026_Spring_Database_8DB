# 팀원 DB 접속 셋업 가이드

이 문서를 따라 하면 **DBeaver로 DB 보기**, **Java 프로젝트에서 JDBC 연결**까지 가능합니다.

소요 시간: 약 20분.

---

## 사전 준비물 (팀장한테 받기)

별도 채널(예: DM)로 따로따로 받으세요.

| 항목 | 받을 곳 | 비고 |
|---|---|---|
| `db8_dev_key` (private SSH key) | 팀장 DM | 절대 GitHub 업로드 금지 |
| MySQL 비밀번호 | 별도 채널 | 키와 같은 채널 X |

---

## 1. SSH 키 저장 (1회)

카톡으로 받은 파일은 `db8_dev_key.txt`예요. 이름 바꾸고 `~/.ssh/`로 옮기고 권한 설정합니다.

### macOS / Linux

```bash
# 카톡에서 받은 파일이 ~/Downloads/db8_dev_key.txt 에 있다고 가정
mkdir -p ~/.ssh
mv ~/Downloads/db8_dev_key.txt ~/.ssh/db8_dev_key
chmod 600 ~/.ssh/db8_dev_key
```

확인:
```bash
ls -la ~/.ssh/db8_dev_key
```
`-rw-------` 로 시작하면 OK.

### Windows (Git Bash 기준)

```bash
mkdir -p ~/.ssh
mv ~/Downloads/db8_dev_key.txt ~/.ssh/db8_dev_key
chmod 600 ~/.ssh/db8_dev_key
```

PowerShell이면 `~/.ssh` 폴더가 없을 수 있어요. 그땐 `mkdir ~/.ssh` 먼저.

---

## 2. SSH 터널 설정

DB 서버는 사설 IP라 직접 접속이 안 됩니다. **VM을 통해 터널을 만들어** 로컬 `localhost:3307` → 원격 MySQL `3306`로 연결합니다.

### ~/.ssh/config 등록 (편의용, 강력 추천)

```bash
# 맥/리눅스
nano ~/.ssh/config
```

아래 내용 붙여넣기 (이미 다른 설정 있으면 맨 아래 추가):

```
Host db8-tunnel
    HostName 134.185.99.179
    User ubuntu
    IdentityFile ~/.ssh/db8_dev_key
    LocalForward 3307 10.0.0.18:3306
    ServerAliveInterval 60
```

저장: `Ctrl+X` → `Y` → `Enter`

### 작업 시작할 때마다 (백그라운드 실행)

```bash
ssh -fN db8-tunnel
```

성공하면 아무 메시지 없이 프롬프트로 돌아옵니다.

### 터널 종료

```bash
# 프로세스 찾아서 종료
pkill -f "ssh -fN db8-tunnel"
```

### 잘 떠 있는지 확인

```bash
lsof -i :3307
```
프로세스가 보이면 터널 살아있음.

> 💡 처음 접속할 땐 `Are you sure you want to continue connecting?` 물어볼 수 있어요 → `yes`

---

## 3. DBeaver 설치 & 연결 (GUI로 DB 보기)

### 설치

- **맥**: `brew install --cask dbeaver-community` (Homebrew 없으면 https://dbeaver.io/download/)
- **윈도우**: https://dbeaver.io/download/ 에서 받기

### 연결 생성

DBeaver 실행 → 상단 메뉴 `Database` → `New Database Connection` → **MySQL** 선택 → Next

#### Main 탭

| 항목 | 값 |
|---|---|
| Server Host | `10.0.0.18` |
| Port | `3306` |
| Database | `db8_bookstore` |
| Username | `dbadmin` |
| Password | (받은 비밀번호) |
| Save password | ✅ 체크 |

#### SSH 탭 (상단 `+ SSH, SSL, ...` 클릭 → `SSH`)

| 항목 | 값 |
|---|---|
| Use SSH Tunnel | (탭에 들어와 있으면 자동 활성) |
| Host/IP | `134.185.99.179` |
| Port | `22` |
| User Name | `ubuntu` |
| Authentication Method | `Public Key` |
| Private key | `~/.ssh/db8_dev_key` |

> Mac에서 파일 선택 창에 `.ssh`가 안 보이면 `Cmd + Shift + .` 눌러 숨김 파일 표시.

#### 테스트 & 저장

1. 하단 **Test tunnel configuration** → 성공 확인
2. 하단 **Test Connection ...** → 드라이버 다운로드 물어보면 → Download → 초록불 확인
3. **Finish**

> 💡 DBeaver는 **자체 SSH 터널을 띄움**. 위에서 만든 `ssh -fN db8-tunnel`이 안 떠 있어도 DBeaver는 동작합니다. (Java용 터널은 따로 필요)

---

## 4. Java 프로젝트 셋업

### 4-1. `db.properties` 만들기

```bash
cd <프로젝트 루트>/DB8/src/main/resources/
cp db.properties.example db.properties
```

`db.properties` 열어서 `YOUR_PASSWORD_HERE` → 받은 비밀번호로 교체.

> ⚠️ `db.properties`는 `.gitignore`에 등록되어 있어 커밋되지 않습니다. **절대 비밀번호를 코드에 하드코딩하거나 example 파일에 적지 마세요.**

### 4-2. SSH 터널 열기 (Java 실행 전 필수)

Java는 DBeaver와 별개로 자체 터널이 필요합니다. 작업 시작 전:

```bash
ssh -fN db8-tunnel
```

### 4-3. 연결 테스트

`Main.java`나 적당한 테스트 코드에서:

```java
import util.DBConnection;
import java.sql.Connection;

public class TestConn {
    public static void main(String[] args) throws Exception {
        try (Connection conn = DBConnection.get()) {
            System.out.println("Connected: " + conn.getCatalog());
        }
    }
}
```

`Connected: db8_bookstore` 출력되면 끝.

---

## 트러블슈팅

### `Communications link failure` / `Connection refused`

→ SSH 터널이 안 떠 있어요. `lsof -i :3307`로 확인 후 `ssh -fN db8-tunnel` 재실행.

### `Permission denied (publickey)`

→ 키 권한 문제. `chmod 600 ~/.ssh/db8_dev_key` 다시.

### `Access denied for user 'dbadmin'`

→ 비밀번호 오타. `db.properties` 확인.

### `Unknown database 'db8_bookstore'`

→ 팀장한테 DB 생성됐는지 확인 요청.

### 포트 3307 이미 사용 중

```bash
lsof -i :3307
```
다른 터널이 떠 있으면 그거 쓰면 되고, 종료하려면 PID로 `kill`.

---

## 보안 체크리스트

- [ ] `db8_dev_key`는 `~/.ssh/`에만 있고 권한은 `600`
- [ ] `db.properties`는 git status에 안 뜸 (gitignored)
- [ ] private key 파일은 슬랙/공용 채널에 절대 업로드 안 함
- [ ] 비밀번호는 코드 어디에도 하드코딩 안 함

---

## 빠른 참고

| 항목 | 값 |
|---|---|
| VM Public IP | `134.185.99.179` |
| MySQL Private IP | `10.0.0.18` |
| MySQL Port | `3306` |
| 로컬 터널 Port | `3307` |
| Database | `db8_bookstore` |
| User | `dbadmin` |
| JDBC URL | `jdbc:mysql://localhost:3307/db8_bookstore` |
