import java.sql.*;
import java.util.Scanner;

public class Test {
    static Connection conn;
    static int role;            //1:学生  2:管理员                //差修改密码的功能
    static int ID;

    public static void main(String[] args) throws Exception {
        connect();
        menu();
    }

    //获取连接
    public static void connect() throws Exception {
        //获取连接
        String url = "jdbc:mysql://localhost:3306/qg";
        String username = "root";
        String password = "123456";
        conn = DriverManager.getConnection(url, username, password);
    }

    public static void menu() throws Exception {
        while (true) {
            System.out.println("===========================");
            System.out.println("    \uD83C\uDF93 学生选课管理系统");
            System.out.println("===========================");
            System.out.println("1. 登录");
            System.out.println("2. 注册");
            System.out.println("3. 退出");
            System.out.print("请选择操作（输入 1-3）：");
            Scanner sc = new Scanner(System.in);
            String choice = sc.next();
            switch (choice) {
                case "1":
                    if (login()) {
                        if (role == 1) {
                            studentMenu();
                        } else if (role == 2) {
                            adminMenu();
                        }
                    }
                    break;
                case "2":
                    register();
                    break;
                case "3":
                    conn.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("输入错误");
                    Thread.sleep(1500);
            }
        }
    }

    //注册
    public static void register() throws Exception {
        System.out.println("===== 用户注册 =====");
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入用户名：");
        System.out.print(">");
        String username = sc.next();
        //检查是否存在
        if (checkName(username)) {
            System.out.println("用户名已存在，请重新注册");
            Thread.sleep(1500);
            return;
        }
        System.out.println("请输入密码：");
        System.out.print(">");
        String password = sc.next();
        System.out.println("请确认密码：");
        System.out.print(">");
        String confirmPassword = sc.next();
        //检查密码是否一致
        if (!password.equals(confirmPassword)) {
            System.out.println("两次输入的密码不一致，请重新注册");
            Thread.sleep(1500);
            return;
        }
        System.out.println("请选择角色 （输入 1 代表学生，2 代表管理员）：");
        String role = sc.next();
        if (!(role.equals("1") || role.equals("2"))) {
            System.out.println("输入错误");
            Thread.sleep(1500);
            return;
        }
        String sql = "insert into users(name,password,role) values(?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, username);
        pstmt.setString(2, password);
        pstmt.setInt(3, Integer.parseInt(role));
        int count = pstmt.executeUpdate();
        if (count > 0) {
            System.out.println("注册成功！请返回主界面登录。");
            Thread.sleep(1500);
        }
        //pstmt.close();


        if (role.equals("1")) {
            //学生注册
            sql = "insert into students(name) values(?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.executeUpdate();
            pstmt.close();
        }
//        } else if (role.equals("2")) {
//            //管理员注册
//            String sql = "insert into admin(name,password) values(?,?)";
//            PreparedStatement pstmt = conn.prepareStatement(sql);
//            pstmt.setString(1, username);
//            pstmt.setString(2, password);
//            int count = pstmt.executeUpdate();
//            if(count > 0) {
//                System.out.println("注册成功");
//            }
//            pstmt.close();
//        }
    }

    public static boolean login() throws Exception {
        System.out.println("===== 用户登录 =====");
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入用户名：");
        System.out.print(">");
        String username = sc.next();
        //检查是否存在
        if (!checkName(username)) {
            System.out.println("用户不存在");
            Thread.sleep(1500);
            return false;
        }
        System.out.println("请输入密码：");
        System.out.print(">");
        String password = sc.next();
        //检查密码是否正确
        if (checkUser(username, password)) {
            if (role == 1) {
                System.out.println("登录成功！你的角色是：学生");
                Thread.sleep(1500);
                return true;
            }
            if (role == 2) {
                System.out.println("登录成功！你的角色是：管理员");
                Thread.sleep(1500);
                return true;
            }
        } else {
            System.out.println("密码错误");
            Thread.sleep(1500);
        }
        return false;
    }

    public static boolean checkName(String username) throws Exception {
        String sql = "select * from users where name = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();
        boolean flag = rs.next();
        rs.close();
        pstmt.close();
        return flag;
    }

    public static boolean checkUser(String username, String password) throws Exception {
        String sql = "select * from users where name = ? and password = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, username);
        pstmt.setString(2, password);
        ResultSet rs = pstmt.executeQuery();
        boolean flag = rs.next();
        if (flag) {
            role = rs.getInt("role");
            getId(username);
        }
        rs.close();
        pstmt.close();
        return flag;
    }

    //从名字中获取id
    public static void getId(String username) throws Exception {
        if (role == 1) {
            String sql = "select id from students where name = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                ID = rs.getInt("id");
            }
            rs.close();
            pstmt.close();
        } else {
            String sql = "select id from users where name = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                ID = rs.getInt("id");
            }
            rs.close();
            pstmt.close();
        }
    }

    public static void studentMenu() throws Exception {
        while (true) {
            System.out.println("===== 学生菜单 =====");
            System.out.println("1. 查看可选课程");
            System.out.println("2. 选择课程");
            System.out.println("3. 退选课程");
            System.out.println("4. 查看已选课程");
            System.out.println("5. 修改手机号");
            System.out.println("6. 退出");
            System.out.print("请选择操作（输入 1-6）：");
            Scanner sc = new Scanner(System.in);
            String choice = sc.next();
            switch (choice) {
                case "1":
                    showCourses();
                    break;
                case "2":
                    selectCourse();
                    break;
                case "3":
                    dropCourse();
                    break;
                case "4":
                    showSelectedCourses();
                    break;
                case "5":
                    updatePhone();
                    break;
                case "6":
                    return;
                default:
                    System.out.println("输入错误");
                    Thread.sleep(1500);
                    break;
            }
        }
    }

    public static void showCourses() throws Exception {
        String sql = "select * from courses";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            System.out.println(rs.getInt("id") + "    " + rs.getString("name") + "    " + rs.getInt("credit"));
        }
        System.out.print("按下回车继续...");
        System.in.read();
    }

    public static void selectCourse() throws Exception {
        //检查是否超过五门
        if (getCourseCount() >= 5) {
            System.out.println("已选择>=5门课程");
            Thread.sleep(1500);
            return;
        }
        System.out.println("请输入课程编号");
        Scanner sc = new Scanner(System.in);
        int course_id = sc.nextInt();
        //检查课程
        if (!checkCourse(course_id)) {
            System.out.println("课程不存在");
            Thread.sleep(1500);
            return;
        }
        //检查是否选过
        if (checkSelectedCourse(course_id)) {
            System.out.println("已选过该课程");
            Thread.sleep(1500);
            return;
        }
        String sql = "insert into student_courses (student_id, course_id) values (?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, ID);
        pstmt.setInt(2, course_id);
        int count = pstmt.executeUpdate();
        if (count > 0) {
            System.out.println("选课成功");
            Thread.sleep(1500);
        }
        pstmt.close();
    }

    public static boolean checkCourse(int course_id) throws Exception {
        String sql = "select * from courses where id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, course_id);
        ResultSet rs = pstmt.executeQuery();
        boolean flag = rs.next();
        rs.close();
        pstmt.close();
        return flag;
    }

    public static void dropCourse() throws Exception {
        System.out.print("请输入课程ID：");
        Scanner sc = new Scanner(System.in);
        int course_id = sc.nextInt();
        if (!checkCourse(course_id)) {
            System.out.println("课程不存在");
            Thread.sleep(1500);
            return;
        }
        //检查是否选过(但是如果没有选过不就是删除失败吗)
        String sql = "delete from student_courses where student_id = ? and course_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, ID);
        pstmt.setInt(2, course_id);
        int count = pstmt.executeUpdate();
        if (count > 0) {
            System.out.println("退课成功");
            Thread.sleep(1500);
        } else {
            System.out.println("您没有选择该课程,退课失败");
            Thread.sleep(1500);
        }
        pstmt.close();
    }

    public static void showSelectedCourses() throws Exception {
        //多表查询
        String sql = "select student_courses.course_id,courses.name,courses.credit from student_courses,courses where student_id = ? and student_courses.course_id = courses.id";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, ID);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            System.out.println(rs.getInt("course_id") + "    " + rs.getString("name") + "    " + rs.getInt("credit"));
        }
        System.out.print("按下回车继续...");
        System.in.read();
        rs.close();
        pstmt.close();
    }

    public static boolean checkSelectedCourse(int course_id) throws Exception {
        String sql = "select * from student_courses where student_id = ? and course_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, ID);
        pstmt.setInt(2, course_id);
        ResultSet rs = pstmt.executeQuery();
        boolean flag = rs.next();
        rs.close();
        pstmt.close();
        return flag;
    }

    public static int getCourseCount() throws Exception {
        String sql = "select count(*) from student_courses,courses where student_id = ? and student_courses.course_id = courses.id";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, ID);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        int count = rs.getInt(1);
        rs.close();
        pstmt.close();
        return count;
    }

    public static void updatePhone() throws Exception {
        //查找目前电话
        String search = "select phone from students where id = ?";
        PreparedStatement pstmt = conn.prepareStatement(search);
        pstmt.setInt(1, ID);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        String phone = rs.getString(1);
        if (phone == null)
            System.out.println("当前电话为空");
        else
            System.out.println("当前电话：" + phone);
        System.out.println("请输入新电话：");
        Scanner sc = new Scanner(System.in);
        String newPhone = sc.next();
        //更新电话
        String sql = "update students set phone = ? where id = ?";
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, newPhone);
        pstmt.setInt(2, ID);
        int count = pstmt.executeUpdate();
        if (count > 0) {
            System.out.println("修改成功");
            Thread.sleep(1500);
        }
    }

    public static void adminMenu() throws Exception {
        while (true) {
            System.out.println("===== 管理员菜单 =====");
            System.out.println("1. 查询所有学生");
            System.out.println("2. 修改学生手机号");
            System.out.println("3. 查询所有课程");
            System.out.println("4. 修改课程学分");
            System.out.println("5. 查询某课程的学生名单");
            System.out.println("6. 查询某学生的选课情况");
            System.out.println("7. 退出");
            System.out.print("请选择操作（输入 1-7）：");
            Scanner sc = new Scanner(System.in);
            String choice = sc.next();
            switch (choice) {
                case "1":
                    queryAllStudents();
                    break;
                case "2":
                    updateStudentPhone();
                    break;
                case "3":
                    showCourses();
                    break;
                case "4":
                    updateCourseCredit();
                    break;
                case "5":
                    queryStudentsByCourse();
                    break;
                case "6":
                    queryCoursesByStudent();
                    break;
                case "7":
                    return;
                default:
                    System.out.println("输入错误");
                    Thread.sleep(1500);
            }
        }
    }

    public static void queryAllStudents() throws Exception {
        String sql = "select * from students";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            System.out.println(rs.getInt("id") + "    " + rs.getString("name") + "    " + rs.getString("phone"));
        }
        System.out.print("按下回车继续...");
        System.in.read();
        rs.close();
        pstmt.close();
    }

    public static void updateStudentPhone() throws Exception {
        System.out.print("请输入学生id：");
        Scanner sc = new Scanner(System.in);
        int id = sc.nextInt();
        //检查是否有该学生
        if(!checkStudent(id)){
            System.out.println("该学生不存在");
            Thread.sleep(1500);
            return;
        }
        System.out.print("请输入新的电话号码：");
        String phone = sc.next();
        String sql = "update students set phone = ? where id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, phone);
        pstmt.setInt(2, id);
        int count = pstmt.executeUpdate();
        if( count > 0) {
            System.out.println("更新成功");
            Thread.sleep(1500);
        }
        pstmt.close();
    }

    public static boolean checkStudent(int id) throws Exception {
        String sql = "select * from students where id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();
        boolean flag = rs.next();
        rs.close();
        pstmt.close();
        return flag;
    }

    public static void queryStudentsByCourse() throws Exception {
        System.out.print("请输入课程id：");
        Scanner sc = new Scanner(System.in);
        int id = sc.nextInt();
        //检查课程是否存在
        if(!checkCourse(id)) {
            System.out.println("课程不存在");
            Thread.sleep(1500);
            return;
        }
        String sql = "select students.id,students.name,students.phone from student_courses,students where course_id = ? and student_courses.student_id = students.id";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            System.out.println(rs.getInt("id") + "    " + rs.getString("name") + "    " + rs.getString("phone"));
        }
        System.out.print("按下回车继续...");
        System.in.read();
        rs.close();
        pstmt.close();
    }

    public static void queryCoursesByStudent() throws Exception {
        System.out.print("请输入学生id：");
        Scanner sc = new Scanner(System.in);
        int id = sc.nextInt();
        //检查学生是否存在
        if(!checkStudent(id)) {
            System.out.println("学生不存在");
            Thread.sleep(1500);
            return;
        }
        String sql = "select courses.id,courses.name,courses.credit from student_courses,courses where student_id = ? and student_courses.course_id = courses.id";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            System.out.println(rs.getInt("id") + "    " + rs.getString("name") + "    " + rs.getInt("credit"));
        }
        System.out.print("按下回车继续...");
        System.in.read();
        rs.close();
        pstmt.close();
    }

    public static void updateCourseCredit() throws Exception {
        System.out.print("请输入课程id：");
        Scanner sc = new Scanner(System.in);
        int id = sc.nextInt();
        //检查课程是否存在
        if(!checkCourse(id)) {
            System.out.println("课程不存在");
            Thread.sleep(1500);
            return;
        }
        System.out.print("请输入新的学分：");
        int credit = sc.nextInt();
        String sql = "update courses set credit = ? where id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, credit);
        pstmt.setInt(2, id);
        int count = pstmt.executeUpdate();
        if(count > 0) {
            System.out.println("更新成功");
            Thread.sleep(1500);
        }
        pstmt.close();
    }
}
