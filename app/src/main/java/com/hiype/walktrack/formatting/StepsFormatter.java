package com.hiype.walktrack.formatting;

import android.util.Log;

import java.util.Arrays;


//TODO NOT Tested
public class StepsFormatter {

    public String getFormattedString (int stepCount) {
        String stepCount_s;
        StringBuilder final_string = new StringBuilder();
        int stepCount_s_length, start_index = 0, end_index = 2;
        double string_amount;

        stepCount_s = String.valueOf(stepCount);
        stepCount_s_length = stepCount_s.length();

        if(stepCount_s_length >= 4 && stepCount_s_length % 3 == 0) {
            string_amount = stepCount_s_length / 3;
            Log.e("STEPFORMATTER", stepCount + " string can be divided by 3");
        } else if(stepCount_s_length >= 4 && stepCount_s_length % 3 != 0) {
            string_amount = stepCount_s_length / 3;
            string_amount = Math.floor(string_amount);
            Log.e("STEPFORMATTER", stepCount + " string cant be divided by 3");
        } else {
            Log.e("STEPFORMATTER", stepCount + " has less than 4 symbols");
            return stepCount_s;
        }

        Log.e("STEPFORMATTER", "Final string ammount for " + stepCount + " is: " + string_amount);

        for(int i = 0; i < string_amount; i++) {
            String number;
            number = stepCount_s.substring(start_index, end_index);
            final_string.append(number);
            final_string.append(" ");
            Log.e("STEPFORMATTER", "Final string loop: " + final_string);
        }
        return final_string.toString();


//        if(stepCount_s_length >= 4) {
//            for (int i = stepCount_s_length; i > -1; i--) {
//                if(i / 3 == 1) {
//                    stepCount_s = Arrays.toString(stepCount_s.split(" "));
//                }
//            }
//            return stepCount_s;
//        } else {
//            return stepCount_s;
//        }
    }

}
