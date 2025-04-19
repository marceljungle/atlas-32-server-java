package com.atlas32.usecase.atlas.params;

import com.atlas32.domain.atlas.command.Command;
import java.util.Objects;

public class CommandToDeviceParams {

  private final Command command;

  private CommandToDeviceParams(Builder builder) {
    command = builder.command;
  }

  public Command getCommand() {
    return command;
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CommandToDeviceParams that = (CommandToDeviceParams) o;
    return Objects.equals(command, that.command);
  }

  @Override
  public int hashCode() {
    return Objects.hash(command);
  }

  @Override
  public String toString() {
    return "CommandToDeviceParams{"
        + "command=" + command
        + '}';
  }

  public static final class Builder {

    private Command command;

    private Builder() {
    }

    public Builder withCommand(Command command) {
      this.command = command;
      return this;
    }

    public CommandToDeviceParams build() {
      return new CommandToDeviceParams(this);
    }
  }
}
