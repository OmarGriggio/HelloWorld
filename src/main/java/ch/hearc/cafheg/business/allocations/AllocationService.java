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
  private static final String PARENT_1 = "Parent1";
  private static final String PARENT_2 = "Parent2";

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
    System.out.println("Déterminer quel parent a le droit aux allocations");
    if (params.getParent1ActiviteLucrative() && !params.getParent2ActiviteLucrative()) {
      return PARENT_1;
    }
    if (params.getParent2ActiviteLucrative() && !params.getParent1ActiviteLucrative()) {
      return PARENT_2;
    }
    return params.getParent1Salaire().compareTo(params.getParent2Salaire()) > 0 ? PARENT_1 : PARENT_2;
  }
}
