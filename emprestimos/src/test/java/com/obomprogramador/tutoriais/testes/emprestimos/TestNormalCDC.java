package com.obomprogramador.tutoriais.testes.emprestimos;

import static org.fest.assertions.Assertions.assertThat;

import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.core.matcher.JButtonMatcher;
import org.fest.swing.core.matcher.JLabelMatcher;
import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.timing.Pause;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestNormalCDC {

	private FrameFixture window;
	
	@BeforeClass public static void setUpOnce() {
		   FailOnThreadViolationRepaintManager.install();
	}
	 
	@Before public void setUp() {
		CalculadorPrestacao calc = GuiActionRunner.execute(new GuiQuery<CalculadorPrestacao>() {
	      protected CalculadorPrestacao executeInEDT() {
	    	  // Inicializando com taxa padrão de 2,5% e Salário Mínimo de R$ 724,00
	    	  CalculadorPrestacao calc = new CalculadorPrestacao(2.5, 724.00);
	    	  return calc; 
	      }
	  });
	  window = new FrameFixture(calc);
	  window.show();

	}
	
	@Test
	public void testNormalCDC() {
		
		// A janela está visível?
        window.requireVisible();
        
        // O título está correto?
        assertThat(window.component().getTitle()).isEqualTo("Cooperativa ABCD - Cálculo de Empréstimos");
        
        // Vamos fazer um empréstimo OK
        // Capital: R$ 1.000,00, prazo: 12 meses
        GenericTypeMatcher<JFormattedTextField> textMatcher = new GenericTypeMatcher<JFormattedTextField>(JFormattedTextField.class) {
        	  @Override protected boolean isMatching(JFormattedTextField capital) {
        	    return "R$ 0,00".equals(capital.getText());
        	  }
        	};        
        window.textBox(textMatcher).enterText("1000,00");
        GenericTypeMatcher<JFormattedTextField> textMatcher2 = new GenericTypeMatcher<JFormattedTextField>(JFormattedTextField.class) {
      	  @Override protected boolean isMatching(JFormattedTextField capital) {
      	    return "0".equals(capital.getText());
      	  }
      	};           
      	window.textBox(textMatcher2).setText("12");
      	GenericTypeMatcher<JRadioButton> textMatcher3 = new GenericTypeMatcher<JRadioButton>(JRadioButton.class) {
      	  @Override protected boolean isMatching(JRadioButton cdcRadio) {
      	    return "CDC".equals(cdcRadio.getText());
      	  }
      	}; 
      	window.radioButton(textMatcher3).check();
      	window.button(JButtonMatcher.withText("Calcular")).click();
      	Pause.pause();
      	GenericTypeMatcher<JPanel> panelMatcher = new GenericTypeMatcher<JPanel>(JPanel.class) {
        	  @Override protected boolean isMatching(JPanel resultados) {
        		  boolean resultado = false;
        		  TitledBorder border = (TitledBorder) resultados.getBorder();
        		  if (border != null) {
          			  if ("Resultado da simulação".equals(border.getTitle())) {
          				  resultado = true;
          			  }
          		  }
        		  return resultado;
        	  }
        	}; 
    	GenericTypeMatcher<JPanel> msgMatcher = new GenericTypeMatcher<JPanel>(JPanel.class) {
      	  @Override protected boolean isMatching(JPanel resultados) {
      		  boolean resultado = false;
      		  TitledBorder border = (TitledBorder) resultados.getBorder();
      		  if (border != null) {
      			  if ("Atenção:".equals(border.getTitle())) {
      				  resultado = true;
      			  }
      		  }
      		  return resultado;
      	  }
      	};        
      	
      	// Tem que exibir o JPanel com os resultados
        window.panel(panelMatcher).requireVisible();
        
        // Não pode exibir o JPanel com a mensagem de erro
        window.panel(msgMatcher).requireNotVisible();
        
        // Verificando o resultado do cálculo
        window.label(JLabelMatcher.withText("R$ 0,00")).requireVisible();
        window.label(JLabelMatcher.withText("R$ 1.000,00")).requireVisible();
        window.label(JLabelMatcher.withText("R$ 108,33")).requireVisible();
        window.button(JButtonMatcher.withText("Limpar")).requireVisible();
        
        // Agora. vamos clicar para limpar tudo: 
        window.button(JButtonMatcher.withText("Limpar")).click();
        Pause.pause();
        window.panel(panelMatcher).requireNotVisible();
        window.panel(msgMatcher).requireNotVisible();
        
	}

	@After public void tearDown() {
		  window.cleanUp();
		}
}
