package ch.hearc.cafheg.business.allocations;

import java.math.BigDecimal;

public class ParentDroitAllocationParams {
    private String enfantResidence;
    private String parent1Residence;
    private String parent2Residence;
    private Boolean parent1ActiviteLucrative = true;
    private Boolean parent2ActiviteLucrative = true;
    private BigDecimal parent1Salaire;
    private BigDecimal parent2Salaire;
    private Boolean parent1ParentalAuthority = true;
    private Boolean parent2ParentalAuthority = true;
    private Boolean parentsTogether = true;
    private Boolean parent1WorkInChildCanton = true;
    private Boolean parent2WorkInChildCanton = true;
    private Boolean parent1Salaried = true;
    private Boolean parent2Salaried = true;

    // Private constructor
    private ParentDroitAllocationParams(Builder builder) {
        this.enfantResidence = builder.enfantResidence;
        this.parent1Residence = builder.parent1Residence;
        this.parent2Residence = builder.parent2Residence;
        this.parent1ActiviteLucrative = builder.parent1ActiviteLucrative;
        this.parent2ActiviteLucrative = builder.parent2ActiviteLucrative;
        this.parent1Salaire = builder.parent1Salaire;
        this.parent2Salaire = builder.parent2Salaire;
        this.parent1ParentalAuthority = builder.parent1ParentalAuthority;
        this.parent2ParentalAuthority = builder.parent2ParentalAuthority;
        this.parentsTogether = builder.parentsTogether;
        this.parent1WorkInChildCanton = builder.parent1WorkInChildCanton;
        this.parent2WorkInChildCanton = builder.parent2WorkInChildCanton;
        this.parent1Salaried = builder.parent1Salaried;
        this.parent2Salaried = builder.parent2Salaried;
    }

    public void setParent1ActiviteLucrative(Boolean parent1ActiviteLucrative) {
        this.parent1ActiviteLucrative = parent1ActiviteLucrative;
    }

    public void setParent2Residence(String parent2Residence) {
        this.parent2Residence = parent2Residence;
    }

    public void setEnfantResidence(String enfantResidence) {
        this.enfantResidence = enfantResidence;
    }

    public void setParent1Residence(String parent1Residence) {
        this.parent1Residence = parent1Residence;
    }

    public void setParent2ActiviteLucrative(Boolean parent2ActiviteLucrative) {
        this.parent2ActiviteLucrative = parent2ActiviteLucrative;
    }

    public void setParent2Salaire(BigDecimal parent2Salaire) {
        this.parent2Salaire = parent2Salaire;
    }

    public void setParent1Salaire(BigDecimal parent1Salaire) {
        this.parent1Salaire = parent1Salaire;
    }

    public void setParent2ParentalAuthority(Boolean parent2ParentalAuthority) {
        this.parent2ParentalAuthority = parent2ParentalAuthority;
    }

    public void setParentsTogether(Boolean parentsTogether) {
        this.parentsTogether = parentsTogether;
    }

    public void setParent2WorkInChildCanton(Boolean parent2WorkInChildCanton) {
        this.parent2WorkInChildCanton = parent2WorkInChildCanton;
    }

    public Boolean getParent1ActiviteLucrative() {
        return parent1ActiviteLucrative;
    }

    public Boolean getParent2ActiviteLucrative() {
        return parent2ActiviteLucrative;
    }

    public Boolean isParent1ParentalAuthority() {
        return parent1ParentalAuthority;
    }

    public Boolean isParent2ParentalAuthority() {
        return parent2ParentalAuthority;
    }

    public Boolean isParentsTogether() {
        return parentsTogether;
    }

    public String getParent1Residence() {
        return parent1Residence;
    }

    public String getEnfantResidence() {
        return enfantResidence;
    }

    public String getParent2Residence() {
        return parent2Residence;
    }

    public Boolean isParent1WorkInChildCanton() {
        return parent1WorkInChildCanton;
    }

    public Boolean isParent2WorkInChildCanton() {
        return parent2WorkInChildCanton;
    }

    public Boolean isParent1Salaried() {
        return parent1Salaried;
    }

    public Boolean isParent2Salaried() {
        return parent2Salaried;
    }

    public BigDecimal getParent2Salaire() {
        return parent2Salaire;
    }

    public BigDecimal getParent1Salaire() {
        return parent1Salaire;
    }

    public void setParent1ParentalAuthority(Boolean parent1ParentalAuthority) {
        this.parent1ParentalAuthority = parent1ParentalAuthority;
    }

    // Builder class
    public static class Builder {
        private String enfantResidence;
        private String parent1Residence;
        private String parent2Residence;
        private Boolean parent1ActiviteLucrative = true;
        private Boolean parent2ActiviteLucrative = true;
        private BigDecimal parent1Salaire;
        private BigDecimal parent2Salaire;
        private Boolean parent1ParentalAuthority = true;
        private Boolean parent2ParentalAuthority = true;
        private Boolean parentsTogether = true;
        private Boolean parent1WorkInChildCanton = true;
        private Boolean parent2WorkInChildCanton = true;
        private Boolean parent1Salaried = true;
        private Boolean parent2Salaried = true;

        public Builder setEnfantResidence(String enfantResidence) {
            this.enfantResidence = enfantResidence;
            return this;
        }

        public Builder setParent1Residence(String parent1Residence) {
            this.parent1Residence = parent1Residence;
            return this;
        }

        public Builder setParent2Residence(String parent2Residence) {
            this.parent2Residence = parent2Residence;
            return this;
        }

        public Builder setParent1ActiviteLucrative(Boolean parent1ActiviteLucrative) {
            this.parent1ActiviteLucrative = parent1ActiviteLucrative;
            return this;
        }

        public Builder setParent2ActiviteLucrative(Boolean parent2ActiviteLucrative) {
            this.parent2ActiviteLucrative = parent2ActiviteLucrative;
            return this;
        }

        public Builder setParent1Salaire(BigDecimal parent1Salaire) {
            this.parent1Salaire = parent1Salaire;
            return this;
        }

        public Builder setParent2Salaire(BigDecimal parent2Salaire) {
            this.parent2Salaire = parent2Salaire;
            return this;
        }

        public Builder setParent1ParentalAuthority(Boolean parent1ParentalAuthority) {
            this.parent1ParentalAuthority = parent1ParentalAuthority;
            return this;
        }

        public Builder setParent2ParentalAuthority(Boolean parent2ParentalAuthority) {
            this.parent2ParentalAuthority = parent2ParentalAuthority;
            return this;
        }

        public Builder setParentsTogether(Boolean parentsTogether) {
            this.parentsTogether = parentsTogether;
            return this;
        }

        public Builder setParent1WorkInChildCanton(Boolean parent1WorkInChildCanton) {
            this.parent1WorkInChildCanton = parent1WorkInChildCanton;
            return this;
        }

        public Builder setParent2WorkInChildCanton(Boolean parent2WorkInChildCanton) {
            this.parent2WorkInChildCanton = parent2WorkInChildCanton;
            return this;
        }

        public Builder setParent1Salaried(Boolean parent1Salaried) {
            this.parent1Salaried = parent1Salaried;
            return this;
        }

        public Builder setParent2Salaried(Boolean parent2Salaried) {
            this.parent2Salaried = parent2Salaried;
            return this;
        }

        public ParentDroitAllocationParams build() {
            return new ParentDroitAllocationParams(this);
        }
    }
}
