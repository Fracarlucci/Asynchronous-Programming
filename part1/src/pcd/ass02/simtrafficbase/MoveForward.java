package pcd.ass02.simtrafficbase;

import pcd.ass02.simengineseq.Action;

/**
 * Car agent move forward action
 */
public record MoveForward(double distance) implements Action {}
