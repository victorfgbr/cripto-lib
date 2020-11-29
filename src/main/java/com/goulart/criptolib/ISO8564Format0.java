package com.goulart.criptolib;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

/**
 * Biblioteca para encode/decode de um pin com base nas normas da ISO8564
 * no formato 0
 * 
 * Para mais detalhes sobre o formato e a ISO:
 * https://atlassian.idtechproducts.com/confluence/display/KB/PIN+Block+format+ISO+9564+Format+0
 * 
 * @author victor.goulart
 */
public class ISO8564Format0 {

	/**
	 * Encripta o PIN utilizando o PAN como chave.
	 * 
	 * @param pin identificador do usuário
	 * @param pan numero da conta
	 * 
	 * @return pinblock gerado pelo algoritimo
	 */
	public static String encode(String pin, String pan) {
	
		try {
			byte[] bPin = Hex.decodeHex(formatPin(pin).toCharArray());
			byte[] bPan = Hex.decodeHex(formatPan(pan).toCharArray());
			byte[] bPinBlock = new byte [8];
			
			for (int i = 0; i < 8; i++) {
				bPinBlock[i] = (byte) (bPin[i] ^ bPan[i]);
			}
			
			return Hex.encodeHexString(bPinBlock).toUpperCase();
			
		} catch (DecoderException e) {
			throw new RuntimeException("Erro ao realizar decode", e);
		}
	}

	/**
	 * Decripta um pinBlock utilizando o PAN como chave, resgatando o PIN.
	 * 
	 * @param pinBlock pin encriptado.
	 * @param pan      Numero da conta
	 * 
	 * @return o PIN aberto.
	 */
	public static String decode(String pinBlock, String pan) {
		
		try {
			byte[] bPinBlock = Hex.decodeHex(pinBlock.toCharArray());
			byte[] bPan = Hex.decodeHex(formatPan(pan).toCharArray());
			byte[] bPin = new byte [8];
			
			for (int i = 0; i < 8; i++) {
				bPin[i] = (byte) (bPinBlock[i] ^ bPan[i]);
			}
			
			String pinFormated = Hex.encodeHexString(bPin).toUpperCase();
			
			Integer pinLenght = Integer.parseInt(pinFormated.substring(0, 2));
			
			return pinFormated.substring(2, 2 + pinLenght);
			
		} catch (DecoderException e) {
			throw new RuntimeException("Erro ao realizar decode", e);
		}
	}

	/**
	 * Prepara o PIN para ser encritado no algoritimo do formato 0
	 * 
	 * 16 posições, sendo 0LPPPPPPPPPPPPPP 
	 * 0: versão do formato 
	 * L: tamanho do pin 
	 * P: pin, com padding de 'F' à direita.
	 * 
	 * @param pin
	 * @return pin no formato 0
	 */
	private static String formatPin(String pin) {

		String pinLength = StringUtils.leftPad(Integer.toString(pin.length()), 2, '0');
		String pinPadded = StringUtils.rightPad(pin, 14, 'F');

		return pinLength + pinPadded;
	}

	/**
	 * Prepara o PAN para ser usado como chave no algoritimo no formato 0 
	 * 
	 * 16 posições, sendo 0000PPPPPPPPPPPP
	 * P: os 12 digitos mais à direita do PAN sem o digito verificador.
	 * 
	 * @param pan
	 * @return pan no formato 0
	 */
	private static String formatPan(String pan) {
		String rightMostDigits = null;

		if (pan.length() > 12) {
			rightMostDigits = pan.substring(pan.length() - 13, pan.length() - 1);
		} else {
			rightMostDigits = pan;
		}
		
		return StringUtils.leftPad(rightMostDigits, 16, '0');
	}
}
