package gsbs.common.components;

public class Team extends Component{
	private int teamNumber;

	public Team(int teamNumber) {
		this.teamNumber = teamNumber;
	}

	public int getTeamNumber() {
		return teamNumber;
	}

	public void setTeamNumber(int teamNumber) {
		this.teamNumber = teamNumber;
	}
}
