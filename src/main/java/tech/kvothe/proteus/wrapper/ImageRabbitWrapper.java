package tech.kvothe.proteus.wrapper;

import org.imgscalr.Scalr;
import tech.kvothe.proteus.dataModels.TransformationData;

public class ImageRabbitWrapper {
    private TransformationData.Resize resize;
    private TransformationData.Crop crop;
    private Scalr.Rotation rotate;
    private String format;
    private TransformationData.Filters filters;
    private Long imageId;
    private String userEmail;

    public ImageRabbitWrapper(TransformationData.Resize resize,
                              TransformationData.Crop crop,
                              Scalr.Rotation rotate,
                              String format,
                              TransformationData.Filters filters,
                              Long imageId,
                              String userEmail) {
        this.resize = resize;
        this.crop = crop;
        this.rotate = rotate;
        this.format = format;
        this.filters = filters;
        this.imageId = imageId;
        this.userEmail = userEmail;
    }

    public TransformationData.Resize getResize() {
        return resize;
    }

    public void setResize(TransformationData.Resize resize) {
        this.resize = resize;
    }

    public TransformationData.Crop getCrop() {
        return crop;
    }

    public void setCrop(TransformationData.Crop crop) {
        this.crop = crop;
    }

    public Scalr.Rotation getRotate() {
        return rotate;
    }

    public void setRotate(Scalr.Rotation rotate) {
        this.rotate = rotate;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public TransformationData.Filters getFilters() {
        return filters;
    }

    public void setFilters(TransformationData.Filters filters) {
        this.filters = filters;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
