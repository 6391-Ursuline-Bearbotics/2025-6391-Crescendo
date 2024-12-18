// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class Intake extends SubsystemBase {
  private static final int intakePrimaryID = 5;
  private static final int intakeSlowSensorPort = 1;
  private static final int intakeStopSensorPort = 0;
  private SparkMax m_motor;

  private static final double intakeSpeed = 0.9;
  private static final double intakeSlowSpeed = 0.15; // Minimum speed required to move the note
  private DigitalInput noteSlowSensor = new DigitalInput(intakeSlowSensorPort);
  private DigitalInput noteStopSensor = new DigitalInput(intakeStopSensorPort);
  private static final SparkBaseConfig sparkConfig = new SparkMaxConfig();

  /** Creates a new Arm. */
  public Intake() {
    // initialize motor
    m_motor = new SparkMax(intakePrimaryID, MotorType.kBrushless);
    sparkConfig.idleMode(IdleMode.kBrake);
    sparkConfig.voltageCompensation(12);
    m_motor.configure(sparkConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    m_motor.setInverted(true);
  }

  public Command intakeOn() {
    return runOnce(() -> m_motor.setVoltage(intakeSpeed * 12));
  }

  // This is a command for whatever process should take place after the first sensor is tripped
  public Command intakeSlow() {
    return new RunCommand(() -> m_motor.setVoltage(intakeSlowSpeed * 12)).until(() -> !noteStopSensor.get())
        .withTimeout(1.0).andThen(intakeOff());
  }

  // This starts the intake then stops it after 2.5 seconds or if a note is detected
  public Command intakeAutoStop() {
    return new RunCommand(() -> m_motor.setVoltage(intakeSpeed * 12)).until(() -> !noteSlowSensor.get())
        .withTimeout(2.5).andThen(intakeSlow());
  }

  public Command intakeOff() {
    return runOnce(() -> m_motor.setVoltage(0));
  }

  public Command shoot() {
    return run(() -> m_motor.setVoltage(intakeSpeed * 12)).withTimeout(0.3)
        .andThen(intakeOff());
  }

  public Trigger getIntakeSlowSensor() {
    return new Trigger(() -> !noteSlowSensor.get());
  }

  public Trigger getIntakeStopSensor() {
    return new Trigger(() -> !noteStopSensor.get());
  }

  public Boolean getIntakeStop() {
    return !noteStopSensor.get();
  }

  public Boolean getIntakeSlow() {
    return !noteSlowSensor.get();
  }
}
