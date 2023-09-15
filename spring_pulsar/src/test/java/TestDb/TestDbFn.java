package TestDb;

import Database.TestConnectionPoolStatic;
import framework.database.DatabaseAction;
import framework.database.pattern.DataTable;

public class TestDbFn {

    public static void main(String[] args) {
        TestDbFn test = new TestDbFn();
        test.test_fn();
    }

    private void test_fn() {
        DatabaseAction dbAct = new DatabaseAction.Builder()
                .setConnection(TestConnectionPoolStatic.getInstance().getConnection())
                .setSQL("SELECT * FROM flower_studio.studio_member")
                .build();
        DataTable dt = dbAct.query();
        System.out.println(dt);
    }

}
