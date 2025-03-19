package com.example.service;

import com.example.dto.LeakResultDTO;
import com.example.matlab.LeakPredictWrapper;
import com.mathworks.toolbox.javabuilder.MWCharArray;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import org.springframework.stereotype.Service;

@Service
public class LeakPredictService {

    private final LeakPredictWrapper leakPredictWrapper;

    public LeakPredictService() {
        this.leakPredictWrapper = new LeakPredictWrapper();
    }

    public LeakResultDTO predictLeak(String filePath, double fs, String outputFolder, String unused) {
        Object[] matlabResults = leakPredictWrapper.compute(filePath, fs, outputFolder, unused);

        if (matlabResults == null || matlabResults.length < 2) {
            return new LeakResultDTO("Error", 0.0);
        }

        try {
            MWCharArray labelArray = (MWCharArray) matlabResults[0];
            MWNumericArray scoreArray = (MWNumericArray) matlabResults[1];

            String label = labelArray.toString();
            double score = scoreArray.getDouble();  // 单个数值

            return new LeakResultDTO(label, score);
        } catch (Exception e) {
            e.printStackTrace();
            return new LeakResultDTO("Exception", 0.0);
        } finally {
            MWCharArray.disposeArray(matlabResults[0]);
            MWNumericArray.disposeArray(matlabResults[1]);
        }
    }
}
