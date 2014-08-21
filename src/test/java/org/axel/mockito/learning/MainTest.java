/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.axel.mockito.learning;

import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;

/**
 *
 * @author axel
 */
public class MainTest {

    //1. Lets Verify some behavior
    @Test
    public void testList() {
        //mock creation
        List mockedList = mock(List.class);
        //using mock object
        mockedList.add("one");
        mockedList.clear();

        //verification
        verify(mockedList).add("one");
        verify(mockedList).clear();

    }

    //2 How aboute some stubbing
    @Test
    public void howAboutSomeStubbing() {
        //you can mock concrete classes, not only interfaces
        LinkedList mockedList = mock(LinkedList.class);
        //stubbing
        when(mockedList.get(0)).thenReturn("first");
//        when(mockedList.get(1)).thenThrow(new RuntimeException());
        //following prints "first"
        System.out.println(mockedList.get(0));
//        System.out.println(mockedList.get(1));
        //following prints "null" beacuse get (999) was not stubed;
        verify(mockedList).get(0);

    }

    //3 Argument Matchers
    @Test
    public void argumentMatchers() {
        LinkedList mockedList = mock(LinkedList.class);
        //stubbing using built-in anyInt() argument matcher
        when(mockedList.get(anyInt())).thenReturn("element");
        //stubbing using hamcres (lets say is Valid() return your own hamcrest matcher):
//        when(mockedList.contains(argThat(isValid()))).thenReturn("element");
        System.out.println(mockedList.get(999));
        //you can also verify using an argument matcher
        verify(mockedList).get(anyInt());
    }

    //4
    @Test
    public void verifyExactNumberOfIncovations() {
        LinkedList list = mock(LinkedList.class);
        list.add("once");

        list.add("twice");
        list.add("twice");

        list.add("three times");
        list.add("three times");
        list.add("three times");

        //following two verifcations work exactly the same - times(1) is used by default
        verify(list).add("once");
        verify(list, times(1)).add("once");

        //exact number of invocations verification
        verify(list, times(2)).add("twice");
        verify(list, times(3)).add("three times");

        //verifaction using never(). never is an alias to times(0)
        verify(list, never()).add("never happened");

        //verification using atLeast()/atMost()
//        verify(list, atLeastOnce()).add("three times");
//        verify(list, atLeast(2)).add("five times");
//        verify(list, atMost(5)).add("three times");
    }

    //5 Stuvving void mtheods with exceptions
    @Test
    public void StubboingVoidMethodsWithExceptions() {
        LinkedList list = mock(LinkedList.class);
        doThrow(new RuntimeException()).when(list).clear();

    }

    //6 Verificatio in order
    @Test
    public void verificationInOrder() {
        //A single mock whose methods must be invoked in a particular oder
        List singleMock = mock(List.class);
        //using a single mock
        singleMock.add("was added first");
        singleMock.add("was added second");

        //
        InOrder inOrder = inOrder(singleMock);

        //following will make sure that add is first called with "was added first," then with "was added second"
        inOrder.verify(singleMock).add("was added first");
        inOrder.verify(singleMock).add("was added second");

        //B. Multiple mocks that mus be usied in a prituclar order
        List firstMock = mock(List.class);
        List secondMock = mock(List.class);

        //using mocks
        firstMock.add("was called first");
        secondMock.add("was called second");

        //Create in order object passing any mocks that need to be verified in order
        InOrder inOrder1 = inOrder(firstMock, secondMock);

        //following will make sure that firstMock was called before second Mock
        inOrder1.verify(firstMock).add("was called first");
        inOrder1.verify(secondMock).add("was called second");

        //Oh, and A + B can be mixed toghete at will
    }

    //7
    @Test
    public void makingSureInteractionSNeverHappenedOnMock() {
        List mockOne = mock(List.class);
        List mockTwo = mock(List.class);
        List mockThree = mock(List.class);
        mockOne.add("one");
        verify(mockOne).add("one");
        verify(mockOne, never()).add("two");
        verifyZeroInteractions(mockTwo, mockThree);
    }

    //8
    @Test
    public void verifyRedundantInvocation() {
        //using mocks
        List mockedList = mock(List.class);
        mockedList.add("one");
        mockedList.add("two");
        verify(mockedList).add("one");
        //verifyNoMoreInteractions(mockedList);

    }

    //9 Shorthand for mocks Creation - @Mock annotation
    @Mock
    private String testAtMock;

    @Test
    public void shorthHandMocksCreation() {
        //Minimize respective mock creation code
        //Makes the test calss more readable
        //Makes the verification error easier to read beacuse the field name is used to identify the mock
    }

    //10Stubing consecutive calls (Iterator style stubbing)
    public void subbingConsecutiveCalls() {
        OutPrinter outPrinter = mock(OutPrinter.class);
        //First call throws runtime exception
        when(outPrinter.printToSystemOut("some arg")).thenThrow(new RuntimeException()).thenReturn("foo");
        //second call: prints "foo"
        System.out.println(outPrinter.printToSystemOut("some arg"));
        //Any consecutive call: prints "foo" as well (last subbing wins)
        System.out.println(outPrinter.printToSystemOut("some arg"));
        //Alternative shorter version of consecutive stubbing
        when(outPrinter.printToSystemOut("some arg")).thenReturn("one", "two", "three");

        //11 Stuvving with callbacks -- Unecissary
//        
//        when(outPrinter.printToSystemOut(anyString())).thenAnswer(new Answer() {
//           Object answer(InvocationOnMock invocation)  {
//               Object[] args = invocation.getArguments();
//               Object mock = invocation.getMock();
//               return "call with arguments" + args;
//           }
//        });
    }

    public static class OutPrinter {

        public String printToSystemOut(String arg) {
            System.out.println(arg);
            return arg;
        }
    }

    //12 ??
    //13 SPying on real objects
    @Test
    public void spyingOnRealObjects() {
        List list = new LinkedList();
        List spy = spy(list);
        when(spy.size()).thenReturn(100);
        //Using the spy calls the *real* method
        spy.add("one");
        spy.add("two");
        //prints "one" - the first element of a list
        System.out.println(spy.get(0));
        //size() methods was stubbed - 100 is printed
        System.out.println(spy.size());
        verify(spy).add("one");
        verify(spy).add("two");

    }

    //14 Changeing default return value of unstubbed invocations
    /*
     Foo mock = mock(Foo.class,Mocktio.RETURNS_SMART_NULLS);
     Foo mockTwo = mock(Foo.class,new YourOwnAnswer());
     */
    public static class Person {

        public String name;

        public Person(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    //15 --- ?????++
//    @Test
//    public void captureingArgumentsForFutherAssertions() {
//        ArgumentCaptor<Person> argument = ArgumentCaptor.forClass(Person.class);
//        verify(mock)
//    }
    
    //16 Real partial mocks -- ?? Overkill?
    
//    @Test
//    public void realPartialMocks() {
//        List list = spy(new LinkedList());
//        //
    
    //17 Resetting mocks
    @Test
    public void resettingMocks() {
       List mock = mock(List.class);
       when(mock.size()).thenReturn(10);
       mock.add(1);
       reset(mock);
       //at this point the mock forgot any interaactions & stubbing
    }
    
    //19 Aliases for Behaviour Driven Development
    
    

}
