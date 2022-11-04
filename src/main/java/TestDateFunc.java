import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TestDateFunc {
    public static void main(String[] args) throws ParseException {
        Date currentDateTime = null;
        Date currentDate = null;
        boolean daywise = false;

        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        while(true) {
            // don't set currentDate to null inside whileloop
            if (daywise == false) {

                currentDateTime = new Date();
                currentDate = formatter.parse(formatter.format(currentDateTime));
                daywise = true;
            }

            int result = (formatter.parse(formatter.format(new Date()))).compareTo(currentDate);
            if (result!=0) {
                daywise = false;
            }
        }
    }
}
