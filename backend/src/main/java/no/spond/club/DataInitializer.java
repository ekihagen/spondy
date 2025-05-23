package no.spond.club;

import no.spond.club.model.Group;
import no.spond.club.model.MemberType;
import no.spond.club.model.RegistrationForm;
import no.spond.club.repository.RegistrationFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private final RegistrationFormRepository formRepository;
    
    @Autowired
    public DataInitializer(RegistrationFormRepository formRepository) {
        this.formRepository = formRepository;
    }
    
    @Override
    public void run(String... args) throws Exception {
        // Sjekk om data allerede eksisterer
        if (formRepository.count() > 0) {
            return;
        }
        
        // Opprett registreringsskjema
        RegistrationForm form = new RegistrationForm(
                "Spond Fotballklubb - Medlemsregistrering 2024",
                "Velkommen til Spond Fotballklubb! Vi gleder oss til å ha deg som medlem. " +
                "Vennligst fyll ut informasjonen nedenfor for å fullføre din registrering.",
                LocalDate.of(2024, 12, 31)
        );
        
        // Opprett medlemstyper
        MemberType juniorType = new MemberType(
                "Junior (under 18 år)",
                "For spillere under 18 år. Inkluderer treninger, kamper og utstyr.",
                new BigDecimal("500.00")
        );
        juniorType.setForm(form);
        
        MemberType seniorType = new MemberType(
                "Senior (18 år og over)",
                "For spillere 18 år og over. Inkluderer treninger, kamper og sosiale arrangementer.",
                new BigDecimal("800.00")
        );
        seniorType.setForm(form);
        
        MemberType supporterType = new MemberType(
                "Støttemedlem",
                "For de som ønsker å støtte klubben uten å være aktiv spiller.",
                new BigDecimal("200.00")
        );
        supporterType.setForm(form);
        
        // Opprett grupper
        Group menGroup = new Group(
                "Herrelag",
                "Hovedlaget for menn, trener tirsdager og torsdager kl 19:00"
        );
        menGroup.setForm(form);
        
        Group womenGroup = new Group(
                "Damelag",
                "Hovedlaget for kvinner, trener mandager og onsdager kl 18:30"
        );
        womenGroup.setForm(form);
        
        Group youthGroup = new Group(
                "Ungdomslag (13-17 år)",
                "For ungdommer, trener mandager og onsdager kl 17:00"
        );
        youthGroup.setForm(form);
        
        Group childrenGroup = new Group(
                "Barneavdeling (6-12 år)",
                "For barn, trener lørdager kl 10:00"
        );
        childrenGroup.setForm(form);
        
        // Sett opp relasjoner
        form.setMemberTypes(Arrays.asList(juniorType, seniorType, supporterType));
        form.setGroups(Arrays.asList(menGroup, womenGroup, youthGroup, childrenGroup));
        
        // Lagre
        formRepository.save(form);
        
        System.out.println("Testdata initialisert med skjema ID: " + form.getId());
    }
} 