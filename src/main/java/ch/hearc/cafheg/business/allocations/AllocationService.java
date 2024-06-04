package ch.hearc.cafheg.business.allocations;

import ch.hearc.cafheg.infrastructure.persistance.AllocataireMapper;
import ch.hearc.cafheg.infrastructure.persistance.AllocationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class AllocationService {
  static final String PARENT_1 = "Parent1";
  static final String PARENT_2 = "Parent2";

  private final AllocataireMapper allocataireMapper;
  private final AllocationMapper allocationMapper;

  private final Logger log = LoggerFactory.getLogger(AllocationService.class);

  public AllocationService(
          AllocataireMapper allocataireMapper,
          AllocationMapper allocationMapper) {
    this.allocataireMapper = allocataireMapper;
    this.allocationMapper = allocationMapper;
  }

  public List<Allocataire> findAllAllocataires(String likeNom) throws SQLException {
    log.info("Rechercher tous les allocataires");
    return allocataireMapper.findAll(likeNom);
  }

  public List<Allocation> findAllocationsActuelles() throws SQLException {
    return allocationMapper.findAll();
  }

  /**
   * Determines which parent has the right to allocations based on the provided parameters.
   *
   * @param params The parameters for determining the right allocation parent.
   * @return The parent with the right to allocations.
   * @throws IllegalArgumentException If none of the conditions in the method's logic match.
   */
  public String getParentDroitAllocation(ParentDroitAllocationParams params) {
    log.info("Déterminer quel parent a le droit aux allocations");

    // Validate required fields
    if (params.getEnfantResidence() == null) {
      throw new IllegalArgumentException("Enfant Residence cannot be null or empty");
    }
    if (params.getParent1Residence() == null) {
      throw new IllegalArgumentException("Parent 1 Residence cannot be null or empty");
    }
    if (params.getParent2Residence() == null) {
      throw new IllegalArgumentException("Parent 2 Residence cannot be null or empty");
    }
    if (params.getParent1ActiviteLucrative() == null) {
      throw new IllegalArgumentException("Parent 1 Activité Lucrative cannot be null");
    }
    if (params.getParent2ActiviteLucrative() == null) {
      throw new IllegalArgumentException("Parent 2 Activité Lucrative cannot be null");
    }
    if (params.getParent1Salaire() == null) {
      throw new IllegalArgumentException("Parent 1 Salaire cannot be null");
    }
    if (params.getParent2Salaire() == null) {
      throw new IllegalArgumentException("Parent 2 Salaire cannot be null");
    }

    // Branch a: One parent with activité lucrative
    if (params.getParent1ActiviteLucrative() && !params.getParent2ActiviteLucrative()) {
      return PARENT_1;
    }
    if (params.getParent2ActiviteLucrative() && !params.getParent1ActiviteLucrative()) {
      return PARENT_2;
    }

    // Both parents have activité lucrative
    if (params.getParent1ActiviteLucrative() && params.getParent2ActiviteLucrative()) {
      // Branch b: Parental authority
      if (params.isParent1ParentalAuthority() && !params.isParent2ParentalAuthority()) {
        return PARENT_1;
      }
      if (!params.isParent1ParentalAuthority() && params.isParent2ParentalAuthority()) {
        return PARENT_2;
      }

      // Branch c: Parents live separately
      if (!params.isParentsTogether()) {
        if (params.getParent1Residence().equals(params.getEnfantResidence())) {
          return PARENT_1;
        }
        if (params.getParent2Residence().equals(params.getEnfantResidence())) {
          return PARENT_2;
        }
      }

      // Branch d: Parents live together, one parent works in child's canton
      if (params.isParentsTogether()) {
        if (params.isParent1WorkInChildCanton() && !params.isParent2WorkInChildCanton()) {
          return PARENT_1;
        }
        if (params.isParent2WorkInChildCanton() && !params.isParent1WorkInChildCanton()) {
          return PARENT_2;
        }

        // Branch e: Parents live together, both salaried or one salaried, one independent
        if (params.isParent1Salaried() || params.isParent2Salaried()) {
          if (params.getParent1Salaire().compareTo(BigDecimal.valueOf(2000)) < 0) {
            return PARENT_1;
          }
          if (params.getParent2Salaire().compareTo(BigDecimal.valueOf(2000)) < 0) {
            return PARENT_2;
          }
          return params.getParent1Salaire().compareTo(params.getParent2Salaire()) > 0 ? PARENT_1 : PARENT_2;
        }


        // Branch f: Parents live together, both independent
        if (!params.isParent1Salaried() && !params.isParent2Salaried()) {
          return params.getParent1Salaire().compareTo(params.getParent2Salaire()) > 0 ? PARENT_1 : PARENT_2;
        }
      }
    }

    throw new IllegalArgumentException("Invalid parameters for determining right allocation parent.");
  }

  public void updateAllocataire(String avsNumber, String newNom, String newPrenom) {
    Allocataire existingAllocataire = allocataireMapper.findByAvsNumber(avsNumber);
    if (existingAllocataire == null) {
      throw new IllegalArgumentException("Allocataire not found");
    }

    boolean isNomChanged = !existingAllocataire.getNom().equals(newNom);
    boolean isPrenomChanged = !existingAllocataire.getPrenom().equals(newPrenom);

    if (isNomChanged || isPrenomChanged) {
      Allocataire updatedAllocataire = new Allocataire(existingAllocataire.getNoAVS(), newNom, newPrenom);
      allocataireMapper.update(updatedAllocataire);
    } else {
      throw new IllegalArgumentException("No changes detected in nom or prenom");
    }
  }

  public void deleteAllocataire(String avsNumber) {
    log.debug("Deleting allocataire with AVS number: {}", avsNumber);
    Allocataire allocataire = allocataireMapper.findByAvsNumber(avsNumber);
    if (allocataire == null) {
      log.error("Allocataire not found for AVS number: {}", avsNumber);
      throw new IllegalArgumentException("Allocataire not found");
    }
  }
}
