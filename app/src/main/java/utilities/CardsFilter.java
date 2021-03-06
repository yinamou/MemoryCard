package utilities;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.util.TimeUtils;
import android.util.Log;

import com.memorycard.android.memorycardapp.Card;
import com.memorycard.android.memorycardapp.CardsGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static utilities.TimeUtilities.convertToMillisByFrequence;

public class CardsFilter {

    private static final String TAG = "CardsFilter";

    public static boolean checkIsLongTimeNoStudy(Context context, CardsGroup cardsGroup) {

        int frequencyType = SettingsUtilities.getNoStudyFrequency(context);
        int frequencyNoStudy = SettingsUtilities.getNoStudyFrequencyValue(context);

        long longFrequenceValue = TimeUtilities.convertToMillisByFrequence(frequencyType, frequencyNoStudy);

        long lastPlayTimeInMillis = cardsGroup.getlLastModifTimeInMillis();

        long currentTime = TimeUtilities.getCurrentTimeInMillies();

        boolean flag = (currentTime - lastPlayTimeInMillis) > longFrequenceValue;
        return flag;
    }



    public static int getCurrentDayofStudy(Context context) {

        int frequency = SettingsUtilities.getStudyFrequency(context);

        long lFrequency = TimeUtilities.convertToMillisByFrequence(frequency,1);

        long installTime = TimeUtilities.getInstallDate(context);

        long currentTime = TimeUtilities.getCurrentTimeInMillies();


        int day = ((currentTime-installTime)/lFrequency)>0?new Long((currentTime-installTime)/lFrequency).intValue():1;


        return day;

    }

    public static List<String> getNoStudyLongTimeGroupList(Context context,List<CardsGroup> cardsGroupList){
        List<String> listname = new ArrayList<>();

        for(CardsGroup cardsGroup:cardsGroupList){
            if(isNoStudyTimeExceed(context,cardsGroup)){
                long lastModif = cardsGroup.getlLastModifTimeInMillis();
                long currentTime = TimeUtilities.getCurrentTimeInMillies();
                long diff = currentTime - lastModif - 60*60*1000;
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                Date date = new Date(diff);
                String res = cardsGroup.getName()+" : " +formatter.format(date);
                listname.add(res);
            }
        }

        return  listname;
    }

    private static boolean isNoStudyTimeExceed(Context context,CardsGroup cardsGroup){
        boolean isExceed = false;

        long lastModif = cardsGroup.getlLastModifTimeInMillis();
        long currentTime = TimeUtilities.getCurrentTimeInMillies();
        int noStudyFrequency = SettingsUtilities.getNoStudyFrequency(context);
        int noStudyFrequencyValue = SettingsUtilities.getNoStudyFrequencyValue(context);
        long interval = TimeUtilities.convertToMillisByFrequence(noStudyFrequency,noStudyFrequencyValue);
        long difference = currentTime - lastModif;
        isExceed = (difference>interval);
        return isExceed;
    }

    private static boolean checkCardDisplay(Card card,int day) {

        if((card.getmDay() == 1) || (card.getmDay() == day) || card.getmDifficultyScore() != 0){
            return true;
        }
        return  false;
    }


    public static List<Card> filterCardsNoDisplay(List<Card> cardList,int day){
        Iterator<Card> it = cardList.iterator();

        while(it.hasNext()){
            Card card = it.next();
            if(!checkCardDisplay(card,day)){
                it.remove();
            }
        }

        return cardList;
    }
}
