package webdata.DictionaryObjects;

//import webdata.DeltaPostingListCompressor;

import webdata.DictionaryObjects.Tables.PostingLists.DeltaPostingListCompressor;
import webdata.Review;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import static webdata.Constants.Features.*;
import static webdata.Constants.SEPARATOR;

public class FeaturesDict implements DictionaryObject, Serializable {

    /*****************THE Dictionary*****************/
    private ArrayList<String> compressedFeaturesDict;


    /*****************GETTERS*****************/
    public ArrayList<String> getCompressedFeaturesDict() {
        return compressedFeaturesDict;
    }


    /*****************CONSTRUCTOR*****************/
    public FeaturesDict() {
        this.compressedFeaturesDict = new ArrayList<>();
    }


    /*****************UPDATE*****************/
    @Override
    public void update(Review review) {
        ArrayList<Integer> values = new ArrayList<>(
                Arrays.asList(
                        review.getScore(),
                        review.getHelpNumerator(),
                        review.getHelpDenominator(),
                        review.getNumOfTokensInReview()
                )
        );

        String intValuesStr = DeltaPostingListCompressor.compressList(values);
        String compressedStr = review.getProductID() + SEPARATOR + intValuesStr;
        compressedFeaturesDict.add(compressedStr);
    }


    /*****************READER FUNCTIONS****************/

    public String getProductID(int reviewID) {
        String value = compressedFeaturesDict.get(reviewID);
        return splitOnSeparator(value)[0];
    }


    public int getValue(int reviewID, int type) {
        assert type == SCORE || type == HELP_D || type == HELP_N || type == REVIEW_LEN;
        String value = compressedFeaturesDict.get(reviewID);
        String binaryString = splitOnSeparator(value)[1];
        ArrayList<Integer> values = DeltaPostingListCompressor.deltaDecode(binaryString);
        return values.get(type);
    }


    private static String[] splitOnSeparator(String str) {
        String[] list = str.split(SEPARATOR);
        assert list.length == 2;
        return list;
    }
}

