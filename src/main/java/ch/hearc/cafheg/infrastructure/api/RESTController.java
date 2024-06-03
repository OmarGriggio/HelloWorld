package ch.hearc.cafheg.infrastructure.api;

import ch.hearc.cafheg.business.allocations.Allocataire;
import ch.hearc.cafheg.business.allocations.Allocation;
import ch.hearc.cafheg.business.allocations.AllocationService;
import ch.hearc.cafheg.business.allocations.ParentDroitAllocationParams;
import ch.hearc.cafheg.business.versements.VersementService;
import ch.hearc.cafheg.infrastructure.pdf.PDFExporter;
import ch.hearc.cafheg.infrastructure.persistance.AllocataireMapper;
import ch.hearc.cafheg.infrastructure.persistance.AllocationMapper;
import ch.hearc.cafheg.infrastructure.persistance.EnfantMapper;
import ch.hearc.cafheg.infrastructure.persistance.VersementMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static ch.hearc.cafheg.infrastructure.persistance.Database.inTransaction;

@RestController
@RequestMapping("/allocataires")
public class RESTController {

  private static final Logger log = LoggerFactory.getLogger(RESTController.class);
  private final AllocationService allocationService;
  private final VersementService versementService;

  public RESTController() {
    this.allocationService = new AllocationService(new AllocataireMapper(), new AllocationMapper());
    this.versementService = new VersementService(new VersementMapper(), new AllocataireMapper(),
            new PDFExporter(new EnfantMapper()));
  }

  @PostMapping("/droits/quel-parent")
  public String getParentDroitAllocation(@RequestBody ParentDroitAllocationParams params) {
    log.info("Received params: {}", params);
    try {
      return inTransaction(() -> allocationService.getParentDroitAllocation(params));
    } catch (Exception e) {
      log.error("Error processing request", e);
      throw e;
    }
  }

  @GetMapping
  public List<Allocataire> allocataires(@RequestParam(value = "startsWith", required = false) String start) {
    log.info("Received request to get allocataires with startsWith: {}", start);
    return inTransaction(() -> {
      List<Allocataire> result = allocationService.findAllAllocataires(start);
      log.info("Found {} allocataires", result.size());
      return result;
    });
  }

  @GetMapping("/allocations")
  public List<Allocation> allocations() {
    log.info("Received request to get all allocations");
    return inTransaction(() -> {
      List<Allocation> result = allocationService.findAllocationsActuelles();
      log.info("Found {} allocations", result.size());
      return result;
    });
  }

  @GetMapping("/allocations/{year}/somme")
  public BigDecimal sommeAs(@PathVariable("year") int year) {
    log.info("Received request to get somme allocation for year: {}", year);
    return inTransaction(() -> {
      BigDecimal result = versementService.findSommeAllocationParAnnee(year).getValue();
      log.info("Found somme allocation for year {}: {}", year, result);
      return result;
    });
  }

  @GetMapping("/allocations-naissances/{year}/somme")
  public BigDecimal sommeAns(@PathVariable("year") int year) {
    log.info("Received request to get somme allocation naissances for year: {}", year);
    return inTransaction(() -> {
      BigDecimal result = versementService.findSommeAllocationNaissanceParAnnee(year).getValue();
      log.info("Found somme allocation naissances for year {}: {}", year, result);
      return result;
    });
  }

  @GetMapping(value = "/allocataires/{allocataireId}/allocations", produces = MediaType.APPLICATION_PDF_VALUE)
  public byte[] pdfAllocations(@PathVariable("allocataireId") int allocataireId) {
    log.info("Received request to get PDF allocations for allocataireId: {}", allocataireId);
    return inTransaction(() -> {
      byte[] result = versementService.exportPDFAllocataire(allocataireId);
      log.info("Generated PDF allocations for allocataireId: {}", allocataireId);
      return result;
    });
  }

  @GetMapping(value = "/{allocataireId}/versements", produces = MediaType.APPLICATION_PDF_VALUE)
  public byte[] pdfVersements(@PathVariable("allocataireId") int allocataireId) {
    log.info("Received request to get PDF versements for allocataireId: {}", allocataireId);
    return inTransaction(() -> {
      byte[] result = versementService.exportPDFVersements(allocataireId);
      log.info("Generated PDF versements for allocataireId: {}", allocataireId);
      return result;
    });
  }

  @PutMapping("/{id}")
  public void updateAllocataire(@PathVariable("id") long id, @RequestBody Map<String, String> params) {
    String newNom = params.get("nom");
    String newPrenom = params.get("prenom");
    log.info("Received request to update allocataire with id: {}. New nom: {}, new prenom: {}", id, newNom, newPrenom);
    inTransaction(() -> {
      allocationService.updateAllocataire(String.valueOf(id), newNom, newPrenom);
      log.info("Updated allocataire with id: {}", id);
      return null;
    });
  }

  @DeleteMapping("/{id}")
  public void deleteAllocataire(@PathVariable("id") long id) {
    log.info("Received request to delete allocataire with id: {}", id);
    inTransaction(() -> {
      allocationService.deleteAllocataire(String.valueOf(id));
      log.info("Deleted allocataire with id: {}", id);
      return null;
    });
  }
}
