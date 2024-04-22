package com.example.application.service;

import com.example.application.data.entity.Meal;
import com.example.application.data.entity.user.RestaurantOwner;
import com.example.application.data.repository.MealRepository;
import com.example.application.data.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class MealService {

    private final MealRepository mealRepository;
    private final RestaurantRepository restaurantRepository;
    Integer currentRestaurantId = 0;


    public MealService(MealRepository mealRepository, RestaurantRepository restaurantRepository) {
        this.mealRepository = mealRepository;
        this.restaurantRepository = restaurantRepository;
    }

    ///////////////////////////////////////////////////////
    public List<Meal> findAllMeal() {
        return mealRepository.findAll();
    }
    /////////////////////////////////////////////////////////


    public List<Meal> findAllMealsForSearch(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return mealRepository.findByRestaurantId(currentRestaurantId);

        } else {
            return mealRepository.searchAllByRestaurantId(stringFilter);
        }
    }



    public List<Meal> findAllMeals(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return mealRepository.findByRestaurantId(currentRestaurantId);

        } else {
            return mealRepository.search(stringFilter);
        }
    }

    public Meal findMeal(Integer id){
        return mealRepository.findFirstById(id);
    }

    public long countMeals() {
        return mealRepository.count();
    }

    public long countMealsById(int currentRestaurantId) {
        List<Meal> allMealsByRestaurantId = mealRepository.findByRestaurantId(currentRestaurantId);
        long zaehler = 0;
        for (Meal i : allMealsByRestaurantId) {
            zaehler++;
        }
        return zaehler;
    }

    public List<Meal> findAllMealsById(int currentRestaurantId) {
        List<Meal> allMealsByRestaurantId = mealRepository.findByRestaurantId(currentRestaurantId);
        return allMealsByRestaurantId;
    }

    public void deleteMeal(Meal meal) {
        mealRepository.delete(meal);
    }

    public void saveMeal(Meal meal) {
        if (meal == null) {
            System.err.println("meal is Null");
            return;
        }
        meal.setRestaurantId(currentRestaurantId);
        mealRepository.save(meal);
    }

    /////////////////////////////////////////////////////////
    public void saveMeal2(Meal meal) {
        if (meal == null) {
            System.err.println("Ordering is NULL");
            return;
        }
        mealRepository.save(meal);
    }
    /////////////////////////////////////////////////////////

    public void saveMealsFromXml(String xml) { // Quelle: Gruppe 5
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

        try {
            builderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            Document xmlDoc = builder.parse(is);
            xmlDoc.getDocumentElement().normalize();

            NodeList list = xmlDoc.getElementsByTagName("gericht");

            for (int i = 0; i < list.getLength(); i++){
                Node node = list.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) node;
                    Meal importMeal = new Meal();

                    String importCategory = element.getParentNode().getNodeName();
                    importCategory = importCategory.substring(0,1).toUpperCase(Locale.ROOT) + importCategory.substring(1);
                    importMeal.setMealCategory(importCategory);
                    importMeal.setMealName(element.getElementsByTagName("name").item(0).getTextContent());
                    importMeal.setMealDetails(element.getElementsByTagName("beschreibung").item(0).getTextContent());
                    importMeal.setMealPrice(Double.parseDouble(element.getElementsByTagName("preis").item(0).getTextContent().replace(",", ".")));
                    saveMeal(importMeal);
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }


    }


    public void setCurrentRestaurantIdByOwner(RestaurantOwner restaurantOwner) {

        this.currentRestaurantId = restaurantRepository.findFirstByUser(restaurantOwner.getId()).getId();
    }

    public void setCurrentRestaurantId(Integer restaurantId){
        this.currentRestaurantId = restaurantId;
    }
}
