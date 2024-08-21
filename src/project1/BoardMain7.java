package project1;

import java.sql.*;

import java.util.Scanner;

public class BoardMain7 {

    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521/xe";
    private static final String DB_USERNAME = "user01";
    private static final String DB_PASSWORD = "1004";
    private static User loginUser = null;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("----------------------------");
            System.out.println("      미니 프로젝트 1차     ");
            System.out.println("-------------------------");
            System.out.println("====== 메뉴 =====");
            System.out.println("1. 회원가입");
            System.out.println("2. 로그인");
            System.out.println("3. 종료");
            System.out.print("원하는 기능: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  //

            switch (choice) {
                case 1:
                    register(scanner);
                    break;
                case 2:
                    login(scanner);
                    break;
                case 3:
                    System.out.println("프로그램을 종료합니다.");
                    scanner.close();
                    return;
                default:
                    System.out.println("잘못된 선택입니다. 다시 시도하세요.");
            }
        }
    }

    // 회원가입 메서드
    private static void register(Scanner scanner) {
        System.out.println("====== 회원가입 ======");
        System.out.print("아이디: ");
        String memid = scanner.nextLine();

        System.out.print("비밀번호: ");
        String mempwd = scanner.nextLine();
        
        System.out.print("이름: ");
        String mname = scanner.nextLine();
        
        System.out.print("성별: ");
        String msex = scanner.nextLine();
        
        System.out.print("전화번호: ");
        String mnum = scanner.nextLine();
        
        User user = new User();
        user.setMemid(memid);
        user.setMempwd(mempwd);
        user.setMname(mname);
        user.setMsex(msex);
        user.setMnum(mnum);
        
        if (insertUser(user)) {
            System.out.println("회원가입이 성공적으로 완료되었습니다.");
        } else {
            System.out.println("회원가입 실패: 아이디가 이미 존재하거나 오류가 발생했습니다.");
        }
    }

 // 로그인 메서드
    private static void login(Scanner scanner) {
        System.out.println("====== 로그인 ======");
        System.out.print("아이디: ");
        String memid = scanner.nextLine();

        System.out.print("비밀번호: ");
        String mempwd = scanner.nextLine();
        User user = new User();
        user.setMemid(memid);
        user.setMempwd(mempwd);
        
        loginUser = authenticate(user);
        if (loginUser != null) {
            System.out.println("로그인 성공! 환영합니다, " + loginUser.getMname()+ "님.");

            // 로그인 시간 기록
            Timestamp loginTime = new Timestamp(System.currentTimeMillis());
            int logId = logLogin(memid, loginTime);

            // 로그인 시간 업데이트
            //logLogin(memid, loginTime);

            System.out.println("현재 로그인 시간: " + loginTime);

            // 게시판 메뉴로 이동
            boardMenu(scanner, memid, logId);
        } else {
            System.out.println("로그인 실패: 아이디 또는 비밀번호가 잘못되었습니다.");
        }
    }


 // 게시판 메뉴 메서드
    private static void boardMenu(Scanner scanner, String memid, int logId) {
        while (true) {
            System.out.println("-------------------------");
            System.out.println("      게시판 메뉴        ");
            System.out.println("-------------------------");
            System.out.println("1. 게시물 목록 조회");
            System.out.println("2. 새 게시물 작성");
            System.out.println("3. 게시물 수정");
            System.out.println("4. 게시물 삭제");
            System.out.println("5. 회원 정보 수정");
            System.out.println("6. 회원 탈퇴");
            System.out.println("7. 로그아웃");
            System.out.print("원하는 기능: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // 버퍼 비우기

            switch (choice) {
                case 1:
                    viewPosts(scanner);
                    break;
                case 2:
                    createPost(scanner, memid);
                    break;
                case 3:
                    updatePost(scanner);
                    break;
                case 4:
                    deletePost(scanner);
                    break;
                case 5:
                    updateUser(scanner, memid);
                    break;
                case 6:
                	deleteUser(scanner);
                	               	
                case 7:
                    // 로그아웃 처리
                	Timestamp logoutTime = new Timestamp(System.currentTimeMillis());
                    logLogout(logId, logoutTime);

                    // 로그아웃 시간 업데이트
                    //logLogout(logId, logoutTime);

                    System.out.println("로그아웃 시간: " + logoutTime);
                    return;  // 로그아웃 후 로그인 메뉴로 돌아가기
                default:
                    System.out.println("잘못된 선택입니다. 다시 시도하세요.");
            }
        }
    }

    // 회원 탈퇴 메서드
    private static void deleteUser(Scanner scanner) {
        System.out.println("====== 회원 탈퇴 ======");
        
             
        System.out.print("비밀번호를 입력하세요: ");
        String mempwd = scanner.nextLine();
        User user = new User();
        user.setMempwd(mempwd);
        
        if (user.getMempwd().equals(loginUser.getMempwd())) {
        	System.out.println("회원정보가 일치합니다.");
        } else {
        	System.out.println("회원정보가 일치하지 않습니다. 탈퇴를 취소합니다.");
        }
               
        Connection conn = null;
        PreparedStatement pstmt = null;
    try {   
	        conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
	        String sql = "UPDATE MEMBER SET STATUS = 'INACTIVE' WHERE MEMPWD = ?";
	        pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, mempwd);
	       
	        int rowsAffected = pstmt.executeUpdate();
	        if (rowsAffected > 0) {
	            System.out.println("정상적으로 탈퇴 되었습니다..");
	        } else {
	            System.out.println("회원 정보가 일치하지 않습니다.");
	        }
        
    	} catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
 // 탈퇴 인증 메서드
    private static User deleteAuthenticate(User user) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            String sql = "SELECT * FROM MEMBER WHERE MEMID = ? AND MEMPWD = ? AND STATUS = 'ACTIVE'";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getMemid());
            pstmt.setString(2, user.getMempwd());
            rs = pstmt.executeQuery();
            
            if(rs.next()) {
            	user.setMemid(rs.getString("MEMID"));
            	user.setMempwd(rs.getString("MEMPWD"));
            	user.setMname(rs.getString("MNAME"));
            	user.setMsex(rs.getString("MSEX"));
            	user.setMnum(rs.getString("MNUM"));
            	return user;
            } else {
            	System.out.println("모르겠다");
                return null;
            }
            	
           } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
        
    // 회원 정보 수정 메서드
    private static void updateUser(Scanner scanner, String memid) {
        System.out.println("====== 회원 정보 수정 ======");
        
        // 비밀번호 확인
        System.out.print("현재 비밀번호를 입력하세요: ");
        String currentPassword = scanner.nextLine();
        User user = new User();
        user.setMemid(memid);
        user.setMempwd(currentPassword);
        
        user = authenticate(user);
        if (user == null) {
            System.out.println("비밀번호가 틀립니다. 회원 정보 수정을 취소합니다.");
            return;
        }
        
        // 새 정보 입력
        System.out.print("새 비밀번호 (기존 비밀번호와 동일한 경우 빈칸으로 두세요): ");
        String newPassword = scanner.nextLine();
        
        System.out.print("새 이름: ");
        String newName = scanner.nextLine();
        
        System.out.print("새 성별: ");
        String newSex = scanner.nextLine();
        
        System.out.print("새 전화번호: ");
        String newNum = scanner.nextLine();
        
        user.setMempwd(newPassword);
        user.setMname(newName);
        user.setMsex(newSex);
        user.setMnum(newNum);
        
        if (updateUserInfo(user)) {
            System.out.println("회원 정보가 성공적으로 수정되었습니다.");
        } else {
            System.out.println("회원 정보 수정 실패.");
        }
    }

    // 회원 정보 수정 DB 메서드
    private static boolean updateUserInfo(User user) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            // 1. 데이터베이스 연결
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            
            // 2. SQL 업데이트 쿼리 준비
            String sql = "UPDATE MEMBER SET MEMPWD = COALESCE(NULLIF(?, ''), MEMPWD), " +
                         "MNAME = COALESCE(NULLIF(?, ''), MNAME), " +
                         "MSEX = COALESCE(NULLIF(?, ''), MSEX), " +
                         "MNUM = COALESCE(NULLIF(?, ''), MNUM) WHERE MEMID = ?";
            
            pstmt = conn.prepareStatement(sql);
            
            // 3. SQL 쿼리에 파라미터 값 설정
            // 필드 값이 비어 있으면 null로 처리하여 업데이트하지 않도록 함
            pstmt.setString(1, user.getMempwd() == null || user.getMempwd().isEmpty() ? null : user.getMempwd());
            pstmt.setString(2, user.getMname() == null || user.getMname().isEmpty() ? null : user.getMname());
            pstmt.setString(3, user.getMsex() == null || user.getMsex().isEmpty() ? null : user.getMsex());
            pstmt.setString(4, user.getMnum() == null || user.getMnum().isEmpty() ? null : user.getMnum());
            pstmt.setString(5, user.getMemid());
            
            // 4. 쿼리 실행
            int rowsAffected = pstmt.executeUpdate();
            
            // 5. 업데이트가 성공적으로 완료되었는지 확인
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                // 6. 자원 해제
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    // 게시물 수정 메서드
    private static void updatePost(Scanner scanner) {
        System.out.println("====== 게시물 수정 ======");
        
        System.out.println("비밀번호를 입력하세요: ");
        String mempwd = scanner.nextLine();
        
        User user = new User();
        user.setMempwd(mempwd);
        
        if (user.getMempwd().equals(loginUser.getMempwd())) {
        	System.out.println("회원정보가 일치합니다.");
        } else {
        	System.out.println("회원정보가 일치하지 않습니다.");
        	return;
        }
        
        System.out.print("수정할 게시물 ID: ");
        int postId = scanner.nextInt();
        scanner.nextLine();  // 버퍼 비우기

        System.out.print("새 제목: ");
        String newTitle = scanner.nextLine();

        System.out.print("새 내용: ");
        String newContent = scanner.nextLine();

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            String sql = "UPDATE POST SET TITLE = ?, CONTENT = ? WHERE POSTNO = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newTitle);
            pstmt.setString(2, newContent);
            pstmt.setInt(3, postId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("게시물이 성공적으로 수정되었습니다.");
            } else {
                System.out.println("수정할 게시물이 존재하지 않습니다.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 게시물 조회수 증가 메서드 (저장 프로시저 호출)
    private static void increaseViewCount(int postId) {
        Connection conn = null;
        CallableStatement cstmt = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            String sql = "{call increase_view_count(?)}";  // 저장 프로시저 호출
            cstmt = conn.prepareCall(sql);
            cstmt.setInt(1, postId);

            cstmt.execute();  // 저장 프로시저 실행

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (cstmt != null) cstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    // 게시물 목록 조회 메서드
    private static void viewPosts(Scanner scanner) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            stmt = conn.createStatement();
            String sql = "SELECT * FROM POST";
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int postId = rs.getInt("POSTNO");
                String writer = rs.getString("WRITER");
                String title = rs.getString("TITLE");
                String content = rs.getString("CONTENT");
                int viewcnt = rs.getInt("VIEWCNT");
                Timestamp createdDate = rs.getTimestamp("WRITEDATE");

                System.out.println("|게시물 번호| " + postId + " | 제목:" + title + " |작성일|" + createdDate + " |");
            }

            System.out.print("상세 내용을 볼 게시물 ID를 입력하세요 (0을 입력하면 취소): ");
            int selectedPostId = scanner.nextInt();
            scanner.nextLine();  // 버퍼 비우기

            if (selectedPostId != 0) {
                viewPostDetail(selectedPostId);
            }

        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 게시물 상세 조회 메서드
    private static void viewPostDetail(int postId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // 조회수 증가
            increaseViewCount(postId);

            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            String sql = "SELECT * FROM POST WHERE POSTNO = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, postId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                String writer = rs.getString("WRITER");
                String title = rs.getString("TITLE");
                String content = rs.getString("CONTENT");
                int viewcnt = rs.getInt("VIEWCNT");
                Timestamp createdDate = rs.getTimestamp("WRITEDATE");

                // 게시물 상세 정보 출력
                
                System.out.println("게시물 번호: " + postId);
                System.out.println("작성자: " + writer);
                System.out.println("제목: " + title);
                System.out.println("내용: " + content);
                System.out.println("조회수: " + viewcnt);
                System.out.println("작성일: " + createdDate);
            } else {
                System.out.println("해당 ID의 게시물이 존재하지 않습니다.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 새 게시물 작성 메서드
    private static void createPost(Scanner scanner, String memid) {
        System.out.println("====== 새 게시물 작성 ======");
        System.out.print("제목: ");
        String title = scanner.nextLine();

        System.out.print("내용: ");
        String content = scanner.nextLine();

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            String sql = "INSERT INTO POST (POSTNO, WRITER, TITLE, CONTENT, VIEWCNT, WRITEDATE) VALUES (POST_SEQ.NEXTVAL, ?, ?, ?, 0, SYSDATE)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memid);
            pstmt.setString(2, title);
            pstmt.setString(3, content);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("새 게시물이 성공적으로 등록되었습니다.");
            } else {
                System.out.println("게시물 등록 실패.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 게시물 삭제 메서드
    private static void deletePost(Scanner scanner) {
        System.out.println("====== 게시물 삭제 ======");
        
        System.out.println("비밀번호를 입력해주세요: ");
        String mempwd = scanner.nextLine();
        
        User user = new User();
        user.setMempwd(mempwd);
        
        if (user.getMempwd().equals(loginUser.getMempwd())) {
        	System.out.println("회원정보가 일치합니다.");
        } else {
        	System.out.println("회원정보가 일치하지 않습니다. 탈퇴를 취소합니다.");
        	return;
        }
        
        System.out.print("삭제할 게시물 ID: ");
        int postId = scanner.nextInt();
        scanner.nextLine();  // 버퍼 비우기

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            String sql = "DELETE FROM POST WHERE POSTNO = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, postId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("게시물이 성공적으로 삭제되었습니다.");
            } else {
                System.out.println("삭제할 게시물이 존재하지 않습니다.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // DB에 사용자 등록 메서드
    private static boolean insertUser(User user) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            String sql = "INSERT INTO MEMBER (MEMID, MEMPWD, MNAME, MSEX, MNUM) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getMemid());
            pstmt.setString(2, user.getMempwd());
            pstmt.setString(3, user.getMname());
            pstmt.setString(4, user.getMsex());
            pstmt.setString(5, user.getMnum());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 로그인 인증 메서드
    private static User authenticate(User user) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            String sql = "SELECT * FROM MEMBER WHERE MEMID = ? AND MEMPWD = ? AND STATUS = 'ACTIVE'";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getMemid());
            pstmt.setString(2, user.getMempwd());
            rs = pstmt.executeQuery();
            if(rs.next()) {
            	
            	user.setMemid(rs.getString("MEMID"));
            	user.setMempwd(rs.getString("MEMPWD"));
            	user.setMname(rs.getString("MNAME"));
            	user.setMsex(rs.getString("MSEX"));
            	user.setMnum(rs.getString("MNUM"));
            	return user;
            } else {
            	System.out.println("사용자 아이디 또는 비밀번호가 잘못되었습니다.");
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    // 로그인 시간 기록 메서드
    private static int logLogin(String memid, Timestamp loginTime) {
        Connection conn = null;
        PreparedStatement pstmtLoginOut = null;
        PreparedStatement pstmtMemberCheck = null;
        PreparedStatement pstmtMemberUpdate = null;
        PreparedStatement pstmtMemberInsert = null;
        ResultSet rs = null;
        int logId = -1;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            conn.setAutoCommit(false);  // 트랜잭션 시작

            // LOGIN_OUT 테이블에 데이터 삽입
            String loginOutSql = "INSERT INTO LOGIN_OUT (LOG_ID, MEMID, LOGINDATE) VALUES (LOGIN_OUT_SEQ.NEXTVAL, ?, ?)";
            pstmtLoginOut = conn.prepareStatement(loginOutSql, new String[] { "LOG_ID" });
            pstmtLoginOut.setString(1, memid);
            pstmtLoginOut.setTimestamp(2, loginTime);
            pstmtLoginOut.executeUpdate();

            // 생성된 LOG_ID 값 가져오기
            rs = pstmtLoginOut.getGeneratedKeys();
            if (rs.next()) {
                logId = rs.getInt(1); // LOGID 반환
            }

            // MEMBER 테이블에서 해당 MEMID가 있는지 확인
            String memberCheckSql = "SELECT LOG_ID FROM MEMBER WHERE MEMID = ?";
            pstmtMemberCheck = conn.prepareStatement(memberCheckSql);
            pstmtMemberCheck.setString(1, memid);
            rs = pstmtMemberCheck.executeQuery();

            if (rs.next()) {
                // MEMID가 존재하면 해당 LOG_ID와 LOGINDATE 업데이트
                String memberUpdateSql = "UPDATE MEMBER SET LOG_ID = ?, LOGINDATE = ? WHERE MEMID = ?";
                pstmtMemberUpdate = conn.prepareStatement(memberUpdateSql);
                pstmtMemberUpdate.setInt(1, logId);
                pstmtMemberUpdate.setTimestamp(2, loginTime);
                pstmtMemberUpdate.setString(3, memid);
                pstmtMemberUpdate.executeUpdate();
            } else {
                // MEMID가 존재하지 않으면 새로 삽입
                String memberInsertSql = "INSERT INTO MEMBER (LOG_ID, MEMID, LOGINDATE) VALUES (?, ?, ?)";
                pstmtMemberInsert = conn.prepareStatement(memberInsertSql);
                pstmtMemberInsert.setInt(1, logId);
                pstmtMemberInsert.setString(2, memid);
                pstmtMemberInsert.setTimestamp(3, loginTime);
                pstmtMemberInsert.executeUpdate();
            }

            conn.commit();  // 트랜잭션 커밋

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();  // 에러 발생 시 롤백
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmtLoginOut != null) pstmtLoginOut.close();
                if (pstmtMemberCheck != null) pstmtMemberCheck.close();
                if (pstmtMemberUpdate != null) pstmtMemberUpdate.close();
                if (pstmtMemberInsert != null) pstmtMemberInsert.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return logId;
    }




    // 로그아웃 시간 기록 메서드
    private static void logLogout(int logId, Timestamp logoutTime) {
        Connection conn = null;
        PreparedStatement pstmtLoginOut = null;
        PreparedStatement pstmtMember = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            conn.setAutoCommit(false);  // 트랜잭션 시작

            // LOGIN_OUT 테이블에서 로그아웃 시간 업데이트
            String updateLoginOutSql = "UPDATE LOGIN_OUT SET LOGOUTDATE = ? WHERE LOG_ID = ?";
            pstmtLoginOut = conn.prepareStatement(updateLoginOutSql);
            pstmtLoginOut.setTimestamp(1, logoutTime);
            pstmtLoginOut.setInt(2, logId);
            pstmtLoginOut.executeUpdate();

            // MEMBER 테이블에서 로그아웃 시간 업데이트
            String updateMemberSql = "UPDATE MEMBER SET LOGOUTDATE = ? WHERE LOG_ID = ?";
            pstmtMember = conn.prepareStatement(updateMemberSql);
            pstmtMember.setTimestamp(1, logoutTime);
            pstmtMember.setInt(2, logId);
            pstmtMember.executeUpdate();

            conn.commit();  // 트랜잭션 커밋

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();  // 에러 발생 시 롤백
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            try {
                if (pstmtLoginOut != null) pstmtLoginOut.close();
                if (pstmtMember != null) pstmtMember.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}