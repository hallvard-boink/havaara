package com.hallvardlaerum.libs.database;


import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;

public class EntityFilterSpecification<Entitet extends EntitetAktig> implements Specification<Entitet> {
    private ArrayList<SearchCriteria> filtre;
    private OperatorEnum operatorEnum = OperatorEnum.AND;
    public enum OperatorEnum {
        AND,
        OR;
    }


    public EntityFilterSpecification(ArrayList<SearchCriteria> filtere, OperatorEnum operatorEnum) {
        this.filtre = filtere;
        this.operatorEnum = operatorEnum;

    }

    public EntityFilterSpecification() {
        filtre = new ArrayList<>();
    }

    public int antallFiltre(){
        return filtre.size();
    }


    @Override
    public Predicate toPredicate (Root<Entitet> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        ArrayList<Predicate> predikaterArrayList = new ArrayList<>();

        for (SearchCriteria criteria:filtre) {
            if (criteria.getOperation().equalsIgnoreCase(">")) {
                if (root.get(criteria.getKey()).getJavaType() == LocalDate.class) {
                    predikaterArrayList.add(builder.greaterThanOrEqualTo(root.get(criteria.getKey()), (LocalDate)criteria.getValue()));
                } else {
                    predikaterArrayList.add(builder.greaterThanOrEqualTo(root.<String>get(criteria.getKey()), criteria.getValue().toString()));
                }

            } else if (criteria.getOperation().equalsIgnoreCase("<")) {
                if (root.get(criteria.getKey()).getJavaType() == LocalDate.class) {
                    predikaterArrayList.add(builder.lessThanOrEqualTo(root.get(criteria.getKey()), (LocalDate)criteria.getValue()));
                } else {
                    predikaterArrayList.add(builder.lessThanOrEqualTo(root.<String>get(criteria.getKey()), criteria.getValue().toString()));
                }

            } else if (criteria.getOperation().equalsIgnoreCase(":")) {
                if (root.get(criteria.getKey()).getJavaType() == String.class) {
                    predikaterArrayList.add(builder.like(root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%"));
//                } else if (root.get(criteria.getKey()).getJavaType() == Kategori.class) {
//                    System.out.println("Pause here");
                    //predikaterArrayList.add(builder.equal(root.get(criteria.getKey()), criteria.getValue()));
                } else {
                    predikaterArrayList.add(builder.equal(root.get(criteria.getKey()), criteria.getValue()));
                }
            }
        }

        if (predikaterArrayList.size()>1) {
            Predicate[] predikaterArray = new Predicate[predikaterArrayList.size()];
            predikaterArray = predikaterArrayList.toArray(predikaterArray);
            if (operatorEnum == OperatorEnum.AND) {
                return (builder.and(predikaterArray));
            } else if (operatorEnum == OperatorEnum.OR) {
                return (builder.or(predikaterArray));
            } else {
                return (builder.and(predikaterArray));
            }
        } else if (predikaterArrayList.size()==1){
            return predikaterArrayList.get(0);
        } else {
            return null;
        }

    }

}
