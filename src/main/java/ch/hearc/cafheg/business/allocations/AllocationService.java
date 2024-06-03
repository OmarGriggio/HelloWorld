package ch.hearc.cafheg.business.allocations;

import ch.hearc.cafheg.infrastructure.persistance.AllocataireMapper;
import ch.hearc.cafheg.infrastructure.persistance.AllocationMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/allocations")
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

  public List<Allocataire> findAllAllocataires(String likeNom) {
    log.info("Rechercher tous les allocataires");
    return allocataireMapper.findAll(likeNom);
  }

  public List<Allocation> findAllocationsActuelles() {
    return allocationMapper.findAll();
  }

  public String getParentDroitAllocation(ParentDroitAllocationParams params) {
    log.info("Déterminer quel parent a le droit aux allocations");

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
}
