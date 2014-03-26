package com.obomprogramador.tutoriais.testes.emprestimos;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.text.NumberFormatter;

@SuppressWarnings("serial")
public class CalculadorPrestacao extends JFrame {
	
	private static double taxaPadrao;
	private static double salarioMinimo;
	private JFormattedTextField txtValorCapital;
	private JFormattedTextField txtNumeroParcelas;
	private JRadioButton radioCDC;
	private JRadioButton radioFinVeiculo;
	private JRadioButton radioAuxilioDoenca;
	private JLabel       lblValorLiquido;
	private JLabel       lblValorRetido;
	private JLabel       lblValorPrestacao;
	private JPanel		 panelResultado;
	private JPanel		 panelMessage;
	private JLabel		 lblMessage;
	private ButtonGroup	 groupTipo;
	private enum enumTipo {NENHUM, CDC, FINANCIAMENTO_VEICULAR, AUXILIO_DOENCA};
	
	
	
	public CalculadorPrestacao() throws HeadlessException {
		super();
		initComponents();
	}

	public CalculadorPrestacao(double taxaPadrao, double salarioMinimo) {
		this();
		CalculadorPrestacao.taxaPadrao = taxaPadrao;
		CalculadorPrestacao.salarioMinimo = salarioMinimo;
	}

	@SuppressWarnings("incomplete-switch")
	public void calcularEmprestimo() {
		
		this.panelMessage.setVisible(false);
		
		enumTipo tipo = enumTipo.NENHUM;
		if (this.radioCDC.isSelected()) {
			tipo = enumTipo.CDC;
		}
		else if (this.radioFinVeiculo.isSelected()) {
				tipo = enumTipo.FINANCIAMENTO_VEICULAR;
		}
		else if (this.radioAuxilioDoenca.isSelected()) {
				tipo = enumTipo.AUXILIO_DOENCA;
		}
		
		if (tipo.equals(enumTipo.NENHUM)) {
			this.lblMessage.setText("Selectione o tipo de empréstimo!");
			this.panelMessage.setVisible(true);
			return;
		}
		
		NumberFormat formato = NumberFormat.getCurrencyInstance();
		formato.setMaximumFractionDigits(2);
		double capital = 0.0;
		double prazo = 0.0;
		try {
			capital = formato.parse(this.txtValorCapital.getText()).doubleValue();
			prazo   = Double.parseDouble(this.txtNumeroParcelas.getText());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		double juros   = 0.0;
		double montante = 0.0;
		double prestacao = 0.0;
		double limite12salarios = salarioMinimo * 12;
		double limite40salarios = salarioMinimo * 40;
		double limite30salarios = salarioMinimo * 30;
		double limite50salarios = salarioMinimo * 50;
		
		switch (tipo) {
		case CDC:
			if (capital > limite12salarios) {
				this.lblMessage.setText("Valor maior que 12 salários mínimos (" + limite12salarios + ")!");
				this.panelMessage.setVisible(true);
				return;
			}
			if (prazo > 12) {
				this.lblMessage.setText("Prazo maior que 12 meses!");
				this.panelMessage.setVisible(true);
				return;
			}			
			juros = (capital * taxaPadrao * prazo) / 100;
			montante = capital + juros;
			prestacao = montante / prazo;
			this.lblValorLiquido.setText(formato.format(capital));
			this.lblValorRetido.setText(formato.format(0.0));
			this.lblValorPrestacao.setText(formato.format(prestacao));
			break;
		case FINANCIAMENTO_VEICULAR:
			if (capital > limite40salarios) {
				this.lblMessage.setText("Valor maior que 40 salários mínimos (" + limite40salarios + ")!");
				this.panelMessage.setVisible(true);
				return;
			}
			if (capital < limite30salarios) {
				this.lblMessage.setText("Valor menor que 30 salários mínimos (" + limite30salarios + ")!");
				this.panelMessage.setVisible(true);
				return;
			}
			if (prazo > 50) {
				this.lblMessage.setText("Prazo maior que 50 meses!");
				this.panelMessage.setVisible(true);
				return;
			}		
			double parcelaRetida = capital * 0.1;
			BigDecimal valorExato = new BigDecimal(parcelaRetida)  
	        			.setScale(2, RoundingMode.HALF_DOWN);  
			double capitalLiquido = capital - valorExato.doubleValue();
			juros = (capitalLiquido * (taxaPadrao * 0.9) * prazo) / 100;
			montante = capitalLiquido + juros;
			prestacao = montante / prazo;
			this.lblValorLiquido.setText(formato.format(capitalLiquido));
			this.lblValorRetido.setText(formato.format(0.0));
			this.lblValorPrestacao.setText(formato.format(prestacao));

			break;
		case AUXILIO_DOENCA:
			if (capital > limite50salarios) {
				this.lblMessage.setText("Valor maior que 50 salários mínimos (" + limite50salarios + ")!");
				this.panelMessage.setVisible(true);
				return;
			}
			if (prazo > 60) {
				this.lblMessage.setText("Prazo maior que 60 meses!");
				this.panelMessage.setVisible(true);
				return;
			}	
			double caucao = capital * 0.05;
			BigDecimal caucaoExata = new BigDecimal(caucao)  
	        			.setScale(2, RoundingMode.HALF_DOWN);  
			juros = (capital * (taxaPadrao * 0.7) * prazo) / 100;
			montante = capital + juros;
			prestacao = montante / prazo;
			this.lblValorLiquido.setText(formato.format(capital 
					- caucaoExata.doubleValue()));
			this.lblValorRetido.setText(formato.format(caucaoExata.doubleValue()));
			this.lblValorPrestacao.setText(formato.format(prestacao));
			
		}
		
		this.panelResultado.setVisible(true);
	}
	
	public void limparEmprestimo() {
		this.txtValorCapital.setValue(0.0);
		this.txtNumeroParcelas.setValue(0);
		this.panelResultado.setVisible(false);
		this.lblValorLiquido.setText("**********************");
		this.lblValorPrestacao.setText("**********************");
		this.lblValorRetido.setText("**********************");
		this.txtValorCapital.requestFocusInWindow();
		this.panelMessage.setVisible(false);
		groupTipo.clearSelection();
	}
	
	private void initComponents() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		NumberFormat formato = NumberFormat.getCurrencyInstance();
		formato.setMaximumFractionDigits(2);

		NumberFormatter formatCapital = new NumberFormatter(formato);
		formatCapital.setMinimum(0.0);
		formatCapital.setMaximum(10000000.0);
		formatCapital.setAllowsInvalid(false);
		formatCapital.setOverwriteMode(true);
		this.txtValorCapital = new JFormattedTextField(formatCapital);
		this.txtValorCapital.setValue(0.0);
		this.txtValorCapital.setColumns(20);
		
		NumberFormat formato2 = NumberFormat.getIntegerInstance();

		NumberFormatter formatParcelas = new NumberFormatter(formato2);
		formatParcelas.setAllowsInvalid(false);
		formatParcelas.setOverwriteMode(true);
		this.txtNumeroParcelas = new JFormattedTextField(formatParcelas);
		this.txtNumeroParcelas.setValue(0);
		this.txtNumeroParcelas.setColumns(20);
		
		this.radioCDC = new JRadioButton("CDC");
		this.radioFinVeiculo = new JRadioButton("Financiamento de Veículo");
		this.radioAuxilioDoenca = new JRadioButton("Auxílio Doença");
		groupTipo = new ButtonGroup();
		groupTipo.add(radioCDC);
		groupTipo.add(radioFinVeiculo);
		groupTipo.add(radioAuxilioDoenca);
		
		JPanel tela = new JPanel();
		tela.setPreferredSize(new Dimension(800,400));
		this.setResizable(false);
		this.getContentPane().add(tela);
				
		SpringLayout layout = new SpringLayout();
		tela.setLayout(layout);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		
		this.setTitle("Cooperativa ABCD - Cálculo de Empréstimos");
		JLabel lbl1 = new JLabel("Valor do capital");
		tela.add(lbl1);
		layout.putConstraint(SpringLayout.WEST, lbl1,5,SpringLayout.WEST, tela);
		layout.putConstraint(SpringLayout.NORTH, lbl1,5,SpringLayout.NORTH, tela);		

		tela.add(txtValorCapital);
		layout.putConstraint(SpringLayout.WEST, txtValorCapital,5,SpringLayout.EAST, lbl1);
		layout.putConstraint(SpringLayout.NORTH, txtValorCapital,5,SpringLayout.NORTH, tela);
		
		JLabel lbl2 = new JLabel("Número de parcelas");
		tela.add(lbl2);
		layout.putConstraint(SpringLayout.WEST, lbl2,5,SpringLayout.EAST, txtValorCapital);
		layout.putConstraint(SpringLayout.NORTH, lbl2,5,SpringLayout.NORTH, tela);		

		tela.add(this.txtNumeroParcelas);
		layout.putConstraint(SpringLayout.WEST, txtNumeroParcelas,5,SpringLayout.EAST, lbl2);
		layout.putConstraint(SpringLayout.NORTH, txtNumeroParcelas,5,SpringLayout.NORTH, tela);
		
		JPanel panelTipo = new JPanel();
		panelTipo.setBorder(BorderFactory.createTitledBorder("Tipo de Empréstimo"));
		panelTipo.setLayout(new FlowLayout());
		panelTipo.add(radioCDC);
		panelTipo.add(radioFinVeiculo);
		panelTipo.add(radioAuxilioDoenca);
		
		tela.add(panelTipo);
		layout.putConstraint(SpringLayout.WEST, panelTipo,5,SpringLayout.WEST, tela);
		layout.putConstraint(SpringLayout.NORTH, panelTipo,5,SpringLayout.SOUTH, txtNumeroParcelas);
		
		JButton btnCalcular = new JButton("Calcular");
		btnCalcular.addActionListener(new CliqueBotao());
		tela.add(btnCalcular);
		layout.putConstraint(SpringLayout.WEST, btnCalcular,50,SpringLayout.EAST, panelTipo);
		layout.putConstraint(SpringLayout.NORTH, btnCalcular,5,SpringLayout.NORTH, panelTipo);
		
		this.lblValorLiquido = new JLabel("*******************");
		this.lblValorPrestacao = new JLabel("*******************");
		this.lblValorRetido = new JLabel("*******************");
		panelResultado = new JPanel();
		panelResultado.setBorder(BorderFactory.createTitledBorder("Resultado da simulação"));
		panelResultado.setLayout(new GridLayout(0,2));
		panelResultado.add(new JLabel("Valor retido: "));
		panelResultado.add(this.lblValorRetido);
		panelResultado.add(new JLabel("Valor líquido a receber: "));
		panelResultado.add(this.lblValorLiquido);
		panelResultado.add(new JLabel("Valor da Prestação: "));
		panelResultado.add(this.lblValorPrestacao);
		JButton btnLimpar = new JButton("Limpar");
		panelResultado.add(btnLimpar);
		btnLimpar.addActionListener(new cliqueLimpar());
		panelResultado.setVisible(false);
		
		tela.add(panelResultado);
		layout.putConstraint(SpringLayout.WEST, panelResultado,5,SpringLayout.WEST, tela);
		layout.putConstraint(SpringLayout.NORTH, panelResultado,5,SpringLayout.SOUTH, panelTipo);
		
		this.lblMessage = new JLabel("Mensagem de erro");
		panelMessage = new JPanel();
		panelMessage.setLayout(new FlowLayout());
		panelMessage.setBorder(BorderFactory.createTitledBorder("Atenção:"));
		panelMessage.add(this.lblMessage);
		tela.add(panelMessage);
		layout.putConstraint(SpringLayout.WEST, panelMessage,5,SpringLayout.WEST, tela);
		layout.putConstraint(SpringLayout.NORTH, panelMessage,5,SpringLayout.SOUTH, panelResultado);
		panelMessage.setVisible(false);
		
		this.pack();
		int w = this.getSize().width;
		int h = this.getSize().height;
		int x = (dim.width-w)/2;
		int y = (dim.height-h)/2;
		this.setLocation(x, y);
		this.getRootPane().setDefaultButton(btnCalcular);
		this.setVisible(true);
	}

	class CliqueBotao implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			calcularEmprestimo();
		}
		
	}
	
	class cliqueLimpar implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			limparEmprestimo();
		}
		
	}
	
	protected static void createAndShowGUI() {
		CalculadorPrestacao cp = new CalculadorPrestacao(2.5, 724.00);
	}



	public static void main (String [] args) {
		
		if (args.length == 0) {
			System.out.println("Informe o valor da taxa padrao, em valor percentual como primeiro argumento!");
			System.exit(1);
		}
		
		try {
	        taxaPadrao = Double.parseDouble(args[0]);
	    } catch (NumberFormatException e) {
	        System.err.println("Informe o valor da taxa padrao, em valor percentual! Deve ser um número real.");
	        System.exit(1);
	    }
		
		if (args.length < 2) {
			System.out.println("Informe o valor do salário mínimo vigente como segundo argumento!");
			System.exit(1);
		}
		
		try {
	        salarioMinimo = Double.parseDouble(args[1]);
	    } catch (NumberFormatException e) {
	        System.err.println("Informe o valor do salário mínimo vigente! Deve ser um número real.");
	        System.exit(1);
	    }
		
		
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		        createAndShowGUI();
		    }
		});
		
	}

	
}
