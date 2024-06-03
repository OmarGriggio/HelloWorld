package ch.hearc.cafheg.business.allocations;

import java.math.BigDecimal;

public class ParentDroitAllocationParams {
    private String enfantResidence;
    private String parent1Residence;
    private String parent2Residence;
    private boolean parent1ActiviteLucrative;
    private boolean parent2ActiviteLucrative;
    private BigDecimal parent1Salaire;
    private BigDecimal parent2Salaire;
    private boolean parent1ParentalAuthority;
    private boolean parent2ParentalAuthority;
    private boolean parentsTogether;
    private boolean parent1WorkInChildCanton;
    private boolean parent2WorkInChildCanton;
    private boolean parent1Salaried;
    private boolean parent2Salaried;

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

    public void setParent1ActiviteLucrative(boolean b) {
    }

    public void setParent2Residence(String bienne) {
    }

    public void setEnfantResidence(Object o) {
    }

    public void setParent1Residence(Object o) {
    }

    public void setParent2ActiviteLucrative(Object o) {
    }

    public void setParent2Salaire(Object o) {
    }

    public void setParent1Salaire(BigDecimal bigDecimal) {
    }

    public void setParent2ParentalAuthority(boolean b) {
    }

    public void setParentsTogether(boolean b) {
    }

    public void setParent2WorkInChildCanton(boolean b) {
    }

    public boolean getParent1ActiviteLucrative() {
        return parent1ActiviteLucrative;
    }

    public boolean getParent2ActiviteLucrative() {
        return parent2ActiviteLucrative;
    }

    public boolean isParent1ParentalAuthority() {
        return parent1ParentalAuthority;
    }

    public boolean isParent2ParentalAuthority() {
        return parent2ParentalAuthority;
    }

    public boolean isParentsTogether() {
        return parentsTogether;
    }

    public Object getParent1Residence() {
        return parent1Residence;
    }

    public Object getEnfantResidence() {
        return enfantResidence;
    }

    public Object getParent2Residence() {
        return parent2Residence;
    }

    public boolean isParent1WorkInChildCanton() {
        return parent1WorkInChildCanton;
    }

    public boolean isParent2WorkInChildCanton() {
        return parent2WorkInChildCanton;
    }

    public boolean isParent1Salaried() {
        return parent1Salaried;
    }

    public boolean isParent2Salaried() {
        return parent2Salaried;
    }

    public BigDecimal getParent2Salaire() {
        return parent2Salaire;
    }

    public BigDecimal getParent1Salaire() {
        return parent1Salaire;
    }

    // Getters
    // (Include all getters here)

    // Builder class
    public static class Builder {
        private String enfantResidence;
        private String parent1Residence;
        private String parent2Residence;
        private boolean parent1ActiviteLucrative;
        private boolean parent2ActiviteLucrative;
        private BigDecimal parent1Salaire;
        private BigDecimal parent2Salaire;
        private boolean parent1ParentalAuthority;
        private boolean parent2ParentalAuthority;
        private boolean parentsTogether;
        private boolean parent1WorkInChildCanton;
        private boolean parent2WorkInChildCanton;
        private boolean parent1Salaried;
        private boolean parent2Salaried;

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

        public Builder setParent1ActiviteLucrative(boolean parent1ActiviteLucrative) {
            this.parent1ActiviteLucrative = parent1ActiviteLucrative;
            return this;
        }

        public Builder setParent2ActiviteLucrative(boolean parent2ActiviteLucrative) {
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

        public Builder setParent1ParentalAuthority(boolean parent1ParentalAuthority) {
            this.parent1ParentalAuthority = parent1ParentalAuthority;
            return this;
        }

        public Builder setParent2ParentalAuthority(boolean parent2ParentalAuthority) {
            this.parent2ParentalAuthority = parent2ParentalAuthority;
            return this;
        }

        public Builder setParentsTogether(boolean parentsTogether) {
            this.parentsTogether = parentsTogether;
            return this;
        }

        public Builder setParent1WorkInChildCanton(boolean parent1WorkInChildCanton) {
            this.parent1WorkInChildCanton = parent1WorkInChildCanton;
            return this;
        }

        public Builder setParent2WorkInChildCanton(boolean parent2WorkInChildCanton) {
            this.parent2WorkInChildCanton = parent2WorkInChildCanton;
            return this;
        }

        public Builder setParent1Salaried(boolean parent1Salaried) {
            this.parent1Salaried = parent1Salaried;
            return this;
        }

        public Builder setParent2Salaried(boolean parent2Salaried) {
            this.parent2Salaried = parent2Salaried;
            return this;
        }

        public ParentDroitAllocationParams build() {
            return new ParentDroitAllocationParams(this);
        }
    }
}
