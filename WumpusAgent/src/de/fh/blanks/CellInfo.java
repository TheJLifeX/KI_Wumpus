package de.fh.blanks;

import java.util.HashMap;

public class CellInfo
{
	/*private InfoType type;
    private double probability;*/

	/**
	 * Arbitrary number of InfoTypes and their respective probabilities.
	 */
    private HashMap<InfoType, Float> infos;

	/**
	 * Pass arbitrary number of InfoTypes and their respective probabilities in form of a HashMap.
	 * @param infos HashMap
	 */
	public CellInfo(HashMap<InfoType, Float> infos)
    {
    	this.infos = infos;
    }

	/**
	 * Pass one InfoType and it's probability.
	 * @param infoType Type of Information on this Cell
	 * @param probability Probability of the given Information being true.
	 */
	public CellInfo(InfoType infoType, float probability)
	{
		infos = new HashMap<InfoType, Float>();
		infos.put(infoType, probability);
	}

	public HashMap<InfoType, Float> getInfos()
	{
		return infos;
	}

	/**
	 * Returns Probability of given InfoType being on this CellInfo.
	 * @param infoType Given InfoType
	 * @return Probability of given InfoType being on this CellInfo.
	 */
	public float getProbability(InfoType infoType)
	{
		return infos.containsKey(infoType) ? infos.get(infoType) : 0.0f;

		// Das obige entspricht ...
		/*if (infos.containsKey(infoType))
			return infos.get(infoType);
		return 0.0f;*/
	}

	/**
	 * Quick check if there is any chance of the given InfoType being on this CellInfo.
	 * @param infoType Given InfoType.
	 * @return If there is any chanec of the given InfoType being on this CellInfo.
	 */
	public boolean has(InfoType infoType)
	{
		return infos.containsKey(infoType);
	}

	/**
	 * Puts the given InfoType and it's probability on this CellInfo. If the given InfoType already is on this CellInfo, it is overriden by the given one.
	 * @param infoType Given InfoType.
	 * @param probability Given probability for the given InfoType.
	 */
	public void putInfoType(InfoType infoType, float probability)
	{
		infos.put(infoType, probability);
	}

	/**
	 * Return a set of tupels each with Infotype and probability as String.
	 * @return
	 */
	@Override
	public String toString()
	{
		String s = "{";
		for (InfoType i : infos.keySet())
			s += "(" + i + ", " + infos.get(i) + "), ";
		s += "}";
		return s;
	}
}
