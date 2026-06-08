package com.devonfw.tools.ide.tool.nest;

import org.junit.jupiter.api.Test;

import com.devonfw.tools.ide.context.AbstractIdeContextTest;
import com.devonfw.tools.ide.context.IdeTestContext;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

/**
 * Test of {@link Nest}.
 */
@WireMockTest
class NestTest extends AbstractIdeContextTest {

  private static final String PROJECT_NEST = "nest";

  /**
   * Tests if the {@link Nest} install works correctly.
   *
   * @param wireMockRuntimeInfo wireMock server on a random port
   */
  @Test
  void testNestInstall(WireMockRuntimeInfo wireMockRuntimeInfo) {

    // arrange
    IdeTestContext context = newContext(PROJECT_NEST, wireMockRuntimeInfo);
    Nest commandlet = new Nest(context);

    // act
    commandlet.install();

    // assert
    checkInstallation(context);

  }

  /**
   * Tests if the {@link Nest} install works correctly.
   *
   * @param wireMockRuntimeInfo wireMock server on a random port
   */
  @Test
  void testNestUninstall(WireMockRuntimeInfo wireMockRuntimeInfo) {

    // arrange
    IdeTestContext context = newContext(PROJECT_NEST, wireMockRuntimeInfo);
    Nest commandlet = new Nest(context);

    // act I
    commandlet.install();

    // assert I
    checkInstallation(context);

    // act II
    commandlet.uninstall();

    // assert II
    assertThat(context).logAtInfo().hasMessageContaining("npm uninstall -g @nestjs/cli");

    assertThat(context).logAtSuccess().hasMessage("Successfully uninstalled nest");
  }

  /**
   * Tests if {@link Nest} run works correctly.
   *
   * @param wireMockRuntimeInfo wireMock server on a random port
   */
  @Test
  void testNestRun(WireMockRuntimeInfo wireMockRuntimeInfo) {

    // arrange
    IdeTestContext context = newContext(PROJECT_NEST, wireMockRuntimeInfo);
    Nest commandlet = new Nest(context);
    commandlet.arguments.setValue("--version");

    // act
    commandlet.run();

    // assert
    assertThat(context).logAtInfo().hasMessageContaining("nest --version");
  }

  private void checkInstallation(IdeTestContext context) {

    assertThat(context).logAtInfo().hasMessageContaining("npm install -gf @nestjs/cli@11.0.21");

    assertThat(context).logAtSuccess().hasMessageContaining("Successfully installed nest in version 11.0.21");
    assertThat(countMessagesContaining(context, "No CVEs found for version 9.9.2 of tool npm.")).isEqualTo(1);
    assertThat(countMessagesContaining(context, "No CVEs found for version v18.19.1 of tool node.")).isEqualTo(1);
    assertThat(context).logAtWarning().hasNoMessageContaining("Found installation loop:");
  }

  private static long countMessagesContaining(IdeTestContext context, String message) {

    return context.getTestStartContext().getEntries().stream().filter(entry -> entry.message().contains(message)).count();
  }
}
