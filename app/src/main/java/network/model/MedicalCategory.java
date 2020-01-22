package network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;
import java.util.List;

public class MedicalCategory implements Serializable {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("category_title")
    @Expose
    private String name;
    @SerializedName("category_imgLink")
    @Expose
    private String image;
    @SerializedName("category_medical_tests")
    @Expose
    private List<MedicalAnalysis> medicalAnalyses;

    public MedicalCategory() {
    }

    public MedicalCategory(String id, String name, String image, List<MedicalAnalysis> medicalAnalyses) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.medicalAnalyses = medicalAnalyses;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<MedicalAnalysis> getMedicalAnalyses() {
        return medicalAnalyses;
    }

    public void setMedicalAnalyses(List<MedicalAnalysis> medicalAnalyses) {
        this.medicalAnalyses = medicalAnalyses;
    }
}
