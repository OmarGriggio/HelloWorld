package ch.hearc.cafheg.business.allocations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import ch.hearc.cafheg.business.common.Montant;
import ch.hearc.cafheg.infrastructure.persistance.AllocataireMapper;
import ch.hearc.cafheg.infrastructure.persistance.AllocationMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AllocationServiceTest {

  private AllocationService allocationService;

  private AllocataireMapper allocataireMapper;
  private AllocationMapper allocationMapper;


  @BeforeEach
  void setUp() {
    allocataireMapper = Mockito.mock(AllocataireMapper.class);
    allocationMapper = Mockito.mock(AllocationMapper.class);

    allocationService = new AllocationService(allocataireMapper, allocationMapper);
  }

  @Test
  void findAllAllocataires_GivenEmptyAllocataires_ShouldBeEmpty() {
    Mockito.when(allocataireMapper.findAll("Geiser")).thenReturn(Collections.emptyList());
    List<Allocataire> all = allocationService.findAllAllocataires("Geiser");
    assertThat(all).isEmpty();
  }

  @Test
  void findAllAllocataires_Given2Geiser_ShouldBe2() {
    Mockito.when(allocataireMapper.findAll("Geiser"))
            .thenReturn(Arrays.asList(new Allocataire(new NoAVS("1000-2000"), "Geiser", "Arnaud"),
                    new Allocataire(new NoAVS("1000-2001"), "Geiser", "Aurélie")));
    List<Allocataire> all = allocationService.findAllAllocataires("Geiser");
    assertAll(() -> assertThat(all.size()).isEqualTo(2),
            () -> assertThat(all.get(0).getNoAVS()).isEqualTo(new NoAVS("1000-2000")),
            () -> assertThat(all.get(0).getNom()).isEqualTo("Geiser"),
            () -> assertThat(all.get(0).getPrenom()).isEqualTo("Arnaud"),
            () -> assertThat(all.get(1).getNoAVS()).isEqualTo(new NoAVS("1000-2001")),
            () -> assertThat(all.get(1).getNom()).isEqualTo("Geiser"),
            () -> assertThat(all.get(1).getPrenom()).isEqualTo("Aurélie"));
  }

  @Test
  void findAllocationsActuelles() {
    Mockito.when(allocationMapper.findAll())
            .thenReturn(Arrays.asList(new Allocation(new Montant(new BigDecimal(1000)), Canton.NE,
                    LocalDate.now(), null), new Allocation(new Montant(new BigDecimal(2000)), Canton.FR,
                    LocalDate.now(), null)));
    List<Allocation> all = allocationService.findAllocationsActuelles();
    assertAll(() -> assertThat(all.size()).isEqualTo(2),
            () -> assertThat(all.get(0).getMontant()).isEqualTo(new Montant(new BigDecimal(1000))),
            () -> assertThat(all.get(0).getCanton()).isEqualTo(Canton.NE),
            () -> assertThat(all.get(0).getDebut()).isEqualTo(LocalDate.now()),
            () -> assertThat(all.get(0).getFin()).isNull(),
            () -> assertThat(all.get(1).getMontant()).isEqualTo(new Montant(new BigDecimal(2000))),
            () -> assertThat(all.get(1).getCanton()).isEqualTo(Canton.FR),
            () -> assertThat(all.get(1).getDebut()).isEqualTo(LocalDate.now()),
            () -> assertThat(all.get(1).getFin()).isNull());
  }

  @Test
  void getParentDroitAllocation_GivenResidence_ShoulBeParentSameResidence() {
    HashMap<String, Object> map = new HashMap<>();
    map.put("enfantResidence", "Neuchâtel");
    map.put("parent1Residence", "Neuchâtel");
    map.put("parent2Residence", "Bienne");
    map.put("parent1ActiviteLucrative", true);
    map.put("parent2ActiviteLucrative", true);
    map.put("parent1Salaire", 2500);
    map.put("parent2Salaire", 3000);

    Assertions.assertEquals("Parent2", allocationService.getParentDroitAllocation(map));
  }

  @Test
  public void testGetParentDroitAllocation_Parent2_Neuchâtel_Neuchâtel() {
    // Arrange
    AllocationService allocationService = new AllocationService(allocataireMapper, allocationMapper);
    HashMap<String, Object> params = new HashMap<>();
    params.put("enfantResidence", "Neuchâtel");
    params.put("parent1Residence", "Neuchâtel");
    params.put("parent2Residence", "Bienne");
    params.put("parent1ActiviteLucrative", true);
    params.put("parent2ActiviteLucrative", true);
    params.put("parent1Salaire", 2500);
    params.put("parent2Salaire", 3000);

    // Act
    String result = allocationService.getParentDroitAllocation(params);

    // Assert
    assertEquals("Parent2", result);
  }

  @Test
  public void testGetParentDroitAllocation_Parent1_Neuchâtel_Bienne() {
    // Arrange
    AllocationService allocationService = new AllocationService(allocataireMapper, allocationMapper);
    HashMap<String, Object> params = new HashMap<>();
    params.put("enfantResidence", "Neuchâtel");
    params.put("parent1Residence", "Bienne");
    params.put("parent2Residence", "Bienne");
    params.put("parent1ActiviteLucrative", true);
    params.put("parent2ActiviteLucrative", true);
    params.put("parent1Salaire", 2500);
    params.put("parent2Salaire", 3000);

    // Act
    String result = allocationService.getParentDroitAllocation(params);

    // Assert
    assertEquals("Parent1", result);
  }

  @Test
  public void testGetParentDroitAllocation_Parent1_Bienne_Neuchâtel() {
    // Arrange
    AllocationService allocationService = new AllocationService(allocataireMapper, allocationMapper);
    HashMap<String, Object> params = new HashMap<>();
    params.put("enfantResidence", "Bienne");
    params.put("parent1Residence", "Neuchâtel");
    params.put("parent2Residence", "Bienne");
    params.put("parent1ActiviteLucrative", true);
    params.put("parent2ActiviteLucrative", true);
    params.put("parent1Salaire", 2500);
    params.put("parent2Salaire", 3000);

    // Act
    String result = allocationService.getParentDroitAllocation(params);

    // Assert
    assertEquals("Parent1", result);
  }

  @Test
  public void testGetParentDroitAllocation_Parent2_Bienne_Bienne() {
    // Arrange
    AllocationService allocationService = new AllocationService(allocataireMapper, allocationMapper);
    HashMap<String, Object> params = new HashMap<>();
    params.put("enfantResidence", "Bienne");
    params.put("parent1Residence", "Bienne");
    params.put("parent2Residence", "Bienne");
    params.put("parent1ActiviteLucrative", true);
    params.put("parent2ActiviteLucrative", true);
    params.put("parent1Salaire", 2500);
    params.put("parent2Salaire", 3000);

    // Act
    String result = allocationService.getParentDroitAllocation(params);

    // Assert
    assertEquals("Parent2", result);
  }

  @Test
  public void testGetParentDroitAllocation_ThrowsIllegalArgumentException_NullEnfantResidence() {
    // Arrange
    AllocationService allocationService = new AllocationService(allocataireMapper, allocationMapper);
    HashMap<String, Object> params = new HashMap<>();
    params.put("enfantResidence", null);
    params.put("parent1Residence", "Neuchâtel");
    params.put("parent2Residence", "Bienne");
    params.put("parent1ActiviteLucrative", true);
    params.put("parent2ActiviteLucrative", true);
    params.put("parent1Salaire", 2500);
    params.put("parent2Salaire", 3000);

    // Act & Assert
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      allocationService.getParentDroitAllocation(params);
    });
  }

  @Test
  public void testGetParentDroitAllocation_ThrowsIllegalArgumentException_EmptyEnfantResidence() {
    // Arrange
    AllocationService allocationService = new AllocationService(allocataireMapper, allocationMapper);
    HashMap<String, Object> params = new HashMap<>();
    params.put("enfantResidence", "");
    params.put("parent1Residence", "Neuchâtel");
    params.put("parent2Residence", "Bienne");
    params.put("parent1ActiviteLucrative", true);
    params.put("parent2ActiviteLucrative", true);
    params.put("parent1Salaire", 2500);
    params.put("parent2Salaire", 3000);

    // Act & Assert
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      allocationService.getParentDroitAllocation(params);
    });
  }

  @Test
  public void testGetParentDroitAllocation_ThrowsIllegalArgumentException_NullParent1Residence() {
    // Arrange
    AllocationService allocationService = new AllocationService(allocataireMapper, allocationMapper);
    HashMap<String, Object> params = new HashMap<>();
    params.put("enfantResidence", "Neuchâtel");
    params.put("parent1Residence", null);
    params.put("parent2Residence", "Bienne");
    params.put("parent1ActiviteLucrative", true);
    params.put("parent2ActiviteLucrative", true);
    params.put("parent1Salaire", 2500);
    params.put("parent2Salaire", 3000);

    // Act & Assert
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      allocationService.getParentDroitAllocation(params);
    });
  }

  @Test
  public void testGetParentDroitAllocation_ThrowsIllegalArgumentException_EmptyParent1Residence() {
    // Arrange
    AllocationService allocationService = new AllocationService(allocataireMapper, allocationMapper);
    HashMap<String, Object> params = new HashMap<>();
    params.put("enfantResidence", "Neuchâtel");
    params.put("parent1Residence", "");
    params.put("parent2Residence", "Bienne");
    params.put("parent1ActiviteLucrative", true);
    params.put("parent2ActiviteLucrative", true);
    params.put("parent1Salaire", 2500);
    params.put("parent2Salaire", 3000);

    // Act & Assert
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      allocationService.getParentDroitAllocation(params);
    });
  }

  @Test
  public void testGetParentDroitAllocation_ThrowsIllegalArgumentException_EmptyParent2Residence() {
    // Arrange
    AllocationService allocationService = new AllocationService(allocataireMapper, allocationMapper);
    HashMap<String, Object> params = new HashMap<>();
    params.put("enfantResidence", "Neuchâtel");
    params.put("parent1Residence", "Neuchâtel");
    params.put("parent2Residence", "");
    params.put("parent1ActiviteLucrative", true);
    params.put("parent2ActiviteLucrative", true);
    params.put("parent1Salaire", 2500);
    params.put("parent2Salaire", 3000);

    // Act & Assert
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      allocationService.getParentDroitAllocation(params);
    });
  }

  @Test
  public void testGetParentDroitAllocation_ThrowsIllegalArgumentException_NullParent1ActiviteLucrative() {
    // Arrange
    AllocationService allocationService = new AllocationService(allocataireMapper, allocationMapper);
    HashMap<String, Object> params = new HashMap<>();
    params.put("enfantResidence", "Neuchâtel");
    params.put("parent1Residence", "Neuchâtel");
    params.put("parent2Residence", "Bienne");
    params.put("parent1ActiviteLucrative", null);
    params.put("parent2ActiviteLucrative", true);
    params.put("parent1Salaire", 2500);
    params.put("parent2Salaire", 3000);

    // Act & Assert
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      allocationService.getParentDroitAllocation(params);
    });
  }

  @Test
  public void testGetParentDroitAllocation_ThrowsIllegalArgumentException_NullParent2ActiviteLucrative() {
    // Arrange
    AllocationService allocationService = new AllocationService(allocataireMapper, allocationMapper);
    HashMap<String, Object> params = new HashMap<>();
    params.put("enfantResidence", "Neuchâtel");
    params.put("parent1Residence", "Neuchâtel");
    params.put("parent2Residence", "Bienne");
    params.put("parent1ActiviteLucrative", true);
    params.put("parent2ActiviteLucrative", null);
    params.put("parent1Salaire", 2500);
    params.put("parent2Salaire", 3000);

    // Act & Assert
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      allocationService.getParentDroitAllocation(params);
    });
  }

  @Test
  public void testGetParentDroitAllocation_ThrowsIllegalArgumentException_NullParent1Salaire() {
    // Arrange
    AllocationService allocationService = new AllocationService(allocataireMapper, allocationMapper);
    HashMap<String, Object> params = new HashMap<>();
    params.put("enfantResidence", "Neuchâtel");
    params.put("parent1Residence", "Neuchâtel");
    params.put("parent2Residence", "Bienne");
    params.put("parent1ActiviteLucrative", true);
    params.put("parent2ActiviteLucrative", true);
    params.put("parent1Salaire", null);
    params.put("parent2Salaire", 3000);

    // Act & Assert
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      allocationService.getParentDroitAllocation(params);
    });
  }

  @Test
  public void testGetParentDroitAllocation_ThrowsIllegalArgumentException_NullParent2Salaire() {
    // Arrange
    AllocationService allocationService = new AllocationService(allocataireMapper, allocationMapper);
    HashMap<String, Object> params = new HashMap<>();
    params.put("enfantResidence", "Neuchâtel");
    params.put("parent1Residence", "Neuchâtel");
    params.put("parent2Residence", "Bienne");
    params.put("parent1ActiviteLucrative", true);
    params.put("parent2ActiviteLucrative", true);
    params.put("parent1Salaire", 2500);
    params.put("parent2Salaire", null);

    // Act & Assert
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      allocationService.getParentDroitAllocation(params);
    });
  }

  @Test
  public void testGetParentDroitAllocation_ReturnsParent1_Parent1SalaireLessThan2000() {
    // Arrange
    AllocationService allocationService = new AllocationService(allocataireMapper, allocationMapper);
    HashMap<String, Object> params = new HashMap<>();
    params.put("enfantResidence", "Neuchâtel");
    params.put("parent1Residence", "Neuchâtel");
    params.put("parent2Residence", "Bienne");
    params.put("parent1ActiviteLucrative", true);
    params.put("parent2ActiviteLucrative", true);
    params.put("parent1Salaire", 1999);
    params.put("parent2Salaire", 3000);

    // Act
    String result = allocationService.getParentDroitAllocation(params);

    // Assert
    assertEquals("Parent1", result);
  }

  @Test
  public void testGetParentDroitAllocation_ReturnsParent2_Parent2SalaireLessThan2000() {
    // Arrange
    AllocationService allocationService = new AllocationService(allocataireMapper, allocationMapper);
    HashMap<String, Object> params = new HashMap<>();
    params.put("enfantResidence", "Neuchâtel");
    params.put("parent1Residence", "Neuchâtel");
    params.put("parent2Residence", "Bienne");
    params.put("parent1ActiviteLucrative", true);
    params.put("parent2ActiviteLucrative", true);
    params.put("parent1Salaire", 2500);
    params.put("parent2Salaire", 1999);

    // Act
    String result = allocationService.getParentDroitAllocation(params);

    // Assert
    assertEquals("Parent2", result);
  }

  @Test
  public void testGetParentDroitAllocation_ReturnsParent2_Parent1SalaireGreaterThanOrEqualTo2000_Parent2SalaireLessThan2000() {
    // Arrange
    AllocationService allocationService = new AllocationService(allocataireMapper, allocationMapper);
    HashMap<String, Object> params = new HashMap<>();
    params.put("enfantResidence", "Neuchâtel");
    params.put("parent1Residence", "Neuchâtel");
    params.put("parent2Residence", "Bienne");
    params.put("parent1ActiviteLucrative", true);
    params.put("parent2ActiviteLucrative", true);
    params.put("parent1Salaire", 2000);
    params.put("parent2Salaire", 1999);

    // Act
    String result = allocationService.getParentDroitAllocation(params);

    // Assert
    assertEquals("Parent2", result);
  }

  @Test
  public void testGetParentDroitAllocation_ReturnsParent1_Parent1SalaireLessThan2000_Parent2SalaireGreaterThanOrEqualTo2000() {
    // Arrange
    AllocationService allocationService = new AllocationService(allocataireMapper, allocationMapper);
    HashMap<String, Object> params = new HashMap<>();
    params.put("enfantResidence", "Neuchâtel");
    params.put("parent1Residence", "Neuchâtel");
    params.put("parent2Residence", "Bienne");
    params.put("parent1ActiviteLucrative", true);
    params.put("parent2ActiviteLucrative", true);
    params.put("parent1Salaire", 1999);
    params.put("parent2Salaire", 2000);

    // Act
    String result = allocationService.getParentDroitAllocation(params);

    // Assert
    assertEquals("Parent1", result);
  }

  @Test
  public void testGetParentDroitAllocation_ReturnsParent2_Parent1ActiviteLucrativeFalse_Parent2ActiviteLucrativeTrue() {
    // Arrange
    AllocationService allocationService = new AllocationService(allocataireMapper, allocationMapper);
    HashMap<String, Object> params = new HashMap<>();
    params.put("enfantResidence", "Neuchâtel");
    params.put("parent1Residence", "Neuchâtel");
    params.put("parent2Residence", "Bienne");
    params.put("parent1ActiviteLucrative", false);
    params.put("parent2ActiviteLucrative", true);
    params.put("parent1Salaire", 2500);
    params.put("parent2Salaire", 3000);

    // Act
    String result = allocationService.getParentDroitAllocation(params);

    // Assert
    assertEquals("Parent2", result);
  }
  @Test
  public void testGetParentDroitAllocation_ReturnsParent1_Parent1ActiviteLucrativeTrue_Parent2ActiviteLucrativeFalse() {
    // Arrange
    AllocationService allocationService = new AllocationService(allocataireMapper, allocationMapper);
    HashMap<String, Object> params = new HashMap<>();
    params.put("enfantResidence", "Neuchâtel");
    params.put("parent1Residence", "Neuchâtel");
    params.put("parent2Residence", "Bienne");
    params.put("parent1ActiviteLucrative", true);
    params.put("parent2ActiviteLucrative", false);
    params.put("parent1Salaire", 2500);
    params.put("parent2Salaire", 3000);

    // Act
    String result = allocationService.getParentDroitAllocation(params);

    // Assert
    assertEquals("Parent1", result);
  }
}
  /*
  // Headers de la requête HTTP doit contenir "Content-Type: application/json"
  // BODY de la requête HTTP à transmettre afin de tester le endpoint
  {
      "enfantResidence" : "Neuchâtel",
      "parent1Residence" : "Neuchâtel",
      "parent2Residence" : "Bienne",
      "parent1ActiviteLucrative" : true,
      "parent2ActiviteLucrative" : true,
      "parent1Salaire" : 2500,
      "parent2Salaire" : 3000
  }
   */