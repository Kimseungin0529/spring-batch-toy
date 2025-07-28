package project.springbatchtoy.batch.classfier;

import lombok.Setter;
import org.springframework.batch.item.ItemWriter;
import org.springframework.classify.Classifier;
import project.springbatchtoy.batch.domain.ApiRequestVO;

import java.util.HashMap;
import java.util.Map;

@Setter
public class WriterClassifier<C, T> implements Classifier<C, T> {
    private Map<String, ItemWriter<ApiRequestVO>> writerMap = new HashMap<>();

    @Override
    public T classify(C classifiable) {
        return (T) writerMap.get(((ApiRequestVO) classifiable).getProduct().getType());

    }

}

