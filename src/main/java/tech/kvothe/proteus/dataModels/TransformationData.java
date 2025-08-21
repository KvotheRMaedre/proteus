package tech.kvothe.proteus.dataModels;

import org.imgscalr.Scalr;

public class TransformationData {

    private Transformations transformations;

    public Transformations getTransformations() {
        return transformations;
    }

    public void setTransformations(Transformations transformations) {
        this.transformations = transformations;
    }

    public static class Transformations {
        private Resize resize;
        private Crop crop;
        private Scalr.Rotation rotate;
        private String format;
        private Filters filters;

        public Resize getResize() {
            return resize;
        }

        public void setResize(Resize resize) {
            this.resize = resize;
        }

        public Crop getCrop() {
            return crop;
        }

        public void setCrop(Crop crop) {
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

        public Filters getFilters() {
            return filters;
        }

        public void setFilters(Filters filters) {
            this.filters = filters;
        }
    }

    public static class Resize {
        private Integer width;
        private Integer height;

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }
    }

    public static class Crop {
        private Integer width;
        private Integer height;
        private Integer x;
        private Integer y;

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }

        public Integer getX() {
            return x;
        }

        public void setX(Integer x) {
            this.x = x;
        }

        public Integer getY() {
            return y;
        }

        public void setY(Integer y) {
            this.y = y;
        }
    }

    public static class Filters {
        private Boolean grayscale;
        private Boolean sepia;

        public Boolean getGrayscale() {
            return grayscale;
        }

        public void setGrayscale(Boolean grayscale) {
            this.grayscale = grayscale;
        }

        public Boolean getSepia() {
            return sepia;
        }

        public void setSepia(Boolean sepia) {
            this.sepia = sepia;
        }
    }
}
