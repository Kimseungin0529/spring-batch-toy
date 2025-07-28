package project.springbatchtoy.batch.classfier;

import lombok.AllArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.classify.Classifier;
import project.springbatchtoy.batch.domain.ApiRequestVO;
import project.springbatchtoy.batch.domain.ProductVO;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class ProcessorClassifier<C, T> implements Classifier<C, T> {
    private final Map<String, ItemProcessor<ProductVO, ApiRequestVO>> processorMap = new HashMap<>();

    @Override
    public T classify(C classifiable) {
        return (T)processorMap.get(((ProductVO)classifiable).getType());

    }


}
